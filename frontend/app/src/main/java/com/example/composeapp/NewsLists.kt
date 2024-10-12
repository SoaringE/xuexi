package com.example.composeapp

import android.R.attr.value
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun fetchNews(start: Int, newsList: MutableList<NewsProfile>, catagory: String, onStateChanged: (NewsResponse) -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/api/") // Replace with your server's URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val newsProfileService = retrofit.create(NewsProfileService::class.java)
    val newsRequest = NewsRequest(start, 20, catagory)
    val call = newsProfileService.postWantedNews(newsRequest)

    call.enqueue(object : Callback<NewsResponse> {
        override fun onResponse(
            call: Call<NewsResponse>,
            response: Response<NewsResponse>
        ) {
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Log.d("net", "ddddddddd")
                    onStateChanged(responseBody)
                }
                /*
                runOnUiThread {
                    // onStateChanged()
                }*/
            } else {
                // Handle an error response here
                Log.d("Main Activity", "error response")
            }
        }

        override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
            // Handle network failure here
            t.printStackTrace()
        }
    })

}

@Composable
fun ActivityPage(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val newsList = remember {
        mutableStateListOf<NewsProfile>()//.apply { add(NewsProfile("what is wrong", "2024", "hh")) }
    }

    Log.d("aaaaaaaaa", "aaaaaaaaaa")
    /*
    LaunchedEffect(Unit) {
        fetchNews(newsList.size)
    }*/

    var end by rememberSaveable {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (!end && !listState.canScrollForward) {
            fetchNews(newsList.size, newsList, "activity") {response: NewsResponse ->
                newsList.addAll(response.newsProfilesList.map { newsProfile ->
                    NewsProfile(newsProfile.id, if (newsProfile.title.substring(0..1)=="<a") Jsoup.parse(newsProfile.title).select("a").text() else newsProfile.title,newsProfile.time, newsProfile.link)})
                end = response.end
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            newsList,
            key = { news -> news.id },
        ) { news ->
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("url", news.link) // 将参数放入 Intent 中
                    context.startActivity(intent)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.title,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
    }
}

@Composable
fun MeetingPage(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val newsList = remember {
        mutableStateListOf<NewsProfile>()//.apply { add(NewsProfile("what is wrong", "2024", "hh")) }
    }

    Log.d("aaaaaaaaa", "aaaaaaaaaa")
    /*
    LaunchedEffect(Unit) {
        fetchNews(newsList.size)
    }*/

    var end by rememberSaveable {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (!end && !listState.canScrollForward) {
            fetchNews(newsList.size, newsList, "meeting") {response: NewsResponse ->
                newsList.addAll(response.newsProfilesList.map { newsProfile ->
                    NewsProfile(newsProfile.id, if (newsProfile.title.substring(0..1)=="<a") Jsoup.parse(newsProfile.title).select("a").text() else newsProfile.title,newsProfile.time, newsProfile.link)})
                end = response.end
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            newsList,
            key = { news -> news.id }
        ) { news ->
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("url", news.link) // 将参数放入 Intent 中
                    context.startActivity(intent)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.title,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
    }
}

@Composable
fun SurveyPage(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val newsList = remember {
        mutableStateListOf<NewsProfile>()//.apply { add(NewsProfile("what is wrong", "2024", "hh")) }
    }

    Log.d("aaaaaaaaa", "aaaaaaaaaa")
    /*
    LaunchedEffect(Unit) {
        fetchNews(newsList.size)
    }*/

    var end by rememberSaveable {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (!end && !listState.canScrollForward) {
            fetchNews(newsList.size, newsList, "survey") {response: NewsResponse ->
                newsList.addAll(response.newsProfilesList.map { newsProfile ->
                    NewsProfile(newsProfile.id, if (newsProfile.title.substring(0..1)=="<a") Jsoup.parse(newsProfile.title).select("a").text() else newsProfile.title,newsProfile.time, newsProfile.link)})
                end = response.end
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            newsList,
            key = { news -> news.id }
        ) { news ->
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("url", news.link) // 将参数放入 Intent 中
                    context.startActivity(intent)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.title,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
    }
}

@Composable
fun VisitPage(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val newsList = remember {
        mutableStateListOf<NewsProfile>()//.apply { add(NewsProfile("what is wrong", "2024", "hh")) }
    }

    Log.d("aaaaaaaaa", "aaaaaaaaaa")
    /*
    LaunchedEffect(Unit) {
        fetchNews(newsList.size)
    }*/

    var end by rememberSaveable {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (!end && !listState.canScrollForward) {
            fetchNews(newsList.size, newsList, "visit") {response: NewsResponse ->
                newsList.addAll(response.newsProfilesList.map { newsProfile ->
                    NewsProfile(newsProfile.id, if (newsProfile.title.substring(0..1)=="<a") Jsoup.parse(newsProfile.title).select("a").text() else newsProfile.title,newsProfile.time, newsProfile.link)})
                end = response.end
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            newsList,
            key = { news -> news.id }
        ) { news ->
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("url", news.link) // 将参数放入 Intent 中
                    context.startActivity(intent)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.title,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
    }
}

@Composable
fun SpeechPage(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val newsList = remember {
        mutableStateListOf<NewsProfile>()//.apply { add(NewsProfile("what is wrong", "2024", "hh")) }
    }

    Log.d("aaaaaaaaa", "aaaaaaaaaa")
    /*
    LaunchedEffect(Unit) {
        fetchNews(newsList.size)
    }*/

    var end by rememberSaveable {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (!end && !listState.canScrollForward) {
            fetchNews(newsList.size, newsList, "speech") {response: NewsResponse ->
                newsList.addAll(response.newsProfilesList.map { newsProfile ->
                    NewsProfile(newsProfile.id, if (newsProfile.title.substring(0..1)=="<a") Jsoup.parse(newsProfile.title).select("a").text() else newsProfile.title,newsProfile.time, newsProfile.link)})
                end = response.end
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            newsList,
            key = { news -> news.id },
        ) { news ->
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("url", news.link) // 将参数放入 Intent 中
                    context.startActivity(intent)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.title,
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
    }
}

@Composable
fun ArticlePage(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val newsList = remember {
        mutableStateListOf<NewsProfile>()//.apply { add(NewsProfile("what is wrong", "2024", "hh")) }
    }

    Log.d("aaaaaaaaa", "aaaaaaaaaa")
    /*
    LaunchedEffect(Unit) {
        fetchNews(newsList.size)
    }*/

    var end by rememberSaveable {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (!end && !listState.canScrollForward) {
            fetchNews(newsList.size, newsList, "article") {response: NewsResponse ->
                newsList.addAll(response.newsProfilesList.map { newsProfile ->
                    NewsProfile(newsProfile.id, if (newsProfile.title.substring(0..1)=="<a") Jsoup.parse(newsProfile.title).select("a").text() else newsProfile.title,newsProfile.time, newsProfile.link)})
                end = response.end
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            newsList,
            key = { news -> news.id }
        ) { news ->
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("url", news.link) // 将参数放入 Intent 中
                    context.startActivity(intent)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.title,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
    }
}

@Composable
fun CommandPage(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val newsList = remember {
        mutableStateListOf<NewsProfile>()//.apply { add(NewsProfile("what is wrong", "2024", "hh")) }
    }


    var end by rememberSaveable {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(listState.canScrollForward) {
        if (!end && !listState.canScrollForward) {
            fetchNews(newsList.size, newsList, "command") {response: NewsResponse ->
                newsList.addAll(response.newsProfilesList.map { newsProfile ->
                    NewsProfile(newsProfile.id, if (newsProfile.title.substring(0..1)=="<a") Jsoup.parse(newsProfile.title).select("a").text() else newsProfile.title,newsProfile.time, newsProfile.link)})
                end = response.end
            }
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            newsList,
            key = { news -> news.id }
        ) { news ->
            Row(
                modifier = Modifier.clickable {
                    val intent = Intent(context, NewsDetailActivity::class.java)
                    intent.putExtra("url", news.link) // 将参数放入 Intent 中
                    context.startActivity(intent)
                }
            ) {
                Spacer(modifier = Modifier.width(8.dp))

                Column() {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = news.title,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
    }
}