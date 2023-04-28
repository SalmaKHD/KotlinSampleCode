package tasks

import contributors.*
import kotlinx.coroutines.*

suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    // send an HTTP request to GitHub server to get all repos
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    // send multiple HTTP requests to get contributors of each repo using different coroutines
    val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
        // prevent the coroutines from running on the UI thread
        async(Dispatchers.Default) {
            log("starting loading for ${repo.name}")
            delay(3000)
            service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }
    // return all the contributors after ALL coroutines finish
    deferreds.awaitAll().flatten().aggregate()
}