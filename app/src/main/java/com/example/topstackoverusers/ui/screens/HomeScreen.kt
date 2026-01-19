package com.example.topstackoverusers.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.topstackoverusers.R
import com.example.topstackoverusers.StackOverFlowApplication
import com.example.topstackoverusers.data.remote.models.StackOverFlowUser
import com.example.topstackoverusers.viewmodel.UiState

@Composable
fun HomeScreen() {

    val appContainer = (LocalContext.current.applicationContext as StackOverFlowApplication).appContainer
    val viewModel = appContainer.homeViewModel
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(val uiState = state) {
            is UiState.Loading -> LoadIndicator()
            is UiState.Success -> {
                HomeContent(state = uiState, onFollowClick = { userId, isFollowed ->
                    viewModel.followUser(userId, isFollowed)
                } )
            }
            is UiState.Error -> Text(text = "Error")
        }
    }
}

@Composable
private fun LoadIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun HomeContent(
    state: UiState.Success,
    onFollowClick: (userId: Int, isFollowed: Boolean) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = rememberLazyListState(),
        ) {
            items(
                items = state.data,
                key = { it.userId }
            ) { item ->
                ListItem(item = item, onFollowClick = { userId, isFollowed ->
                    onFollowClick(userId,isFollowed)
                })
            }
        }
    }

}

@Composable
private fun ListItem(
    item: StackOverFlowUser,
    onFollowClick: (userId: Int, isFollowed: Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = MaterialTheme.shapes.large,
        onClick = { }
    ) {
        Row{
            item.profileImage?.let {
                Image(
                    bitmap = item.profileImage,
                    contentDescription = "Profile picture",
                )
            } ?: run {
                Image(
                    painter = painterResource(id = R.drawable.placeholder),
                    contentDescription = "Profile picture",
                )
            }

            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(space = 10.dp, alignment = Alignment.CenterVertically)
            ) {
                Text(text = item.displayName ?: "")
                Text(text = item.reputation.toString())
            }

            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        onFollowClick(item.userId, !item.isFollowed)
                    }
            ) {
                Icon(
                    imageVector = if (item.isFollowed) Icons.Filled.Favorite else Icons.Filled.Add,
                    contentDescription = "Follow User",
                    tint = if (item.isFollowed) Color.Red else Color.Gray
                )
            }
        }
    }
}