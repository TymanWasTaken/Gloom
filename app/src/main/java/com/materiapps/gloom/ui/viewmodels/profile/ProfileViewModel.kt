package com.materiapps.gloom.ui.viewmodels.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.apollographql.apollo3.ApolloClient
import com.materiapps.gloom.ProfileQuery
import com.materiapps.gloom.domain.repository.GithubRepository
import com.materiapps.gloom.rest.utils.fold
import com.materiapps.gloom.rest.utils.ifSuccessful
import com.materiapps.gloom.rest.utils.response
import com.materiapps.gloom.utils.scope
import kotlinx.coroutines.launch

class ProfileViewModel(
    client: ApolloClient,
    repo: GithubRepository
) : ScreenModel {

    var user: ProfileQuery.Viewer? by mutableStateOf(null)
    var readMe: String by mutableStateOf("")

    var isLoading by mutableStateOf(true)
    var hasErrors by mutableStateOf(false)

    init {
        scope.launch {
            client.query(ProfileQuery()).response().fold(
                success = {
                    user = it.viewer
                    repo.getRepoReadMe(it.viewer.login, it.viewer.login).ifSuccessful { res ->
                        readMe = res
                    }
                },
                fail = {
                    hasErrors = true
                }
            )
        }
    }

}