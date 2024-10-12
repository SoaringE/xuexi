package com.example.composeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.ui.theme.ComposeAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import org.jsoup.Jsoup


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Entrance()
                }
            }
        }
    }

    @Composable
    fun Entrance(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        var logged by rememberSaveable {
            mutableStateOf(false)
        }

        NavHost(navController = navController, startDestination = if(logged) RouteConfig.ROUTE_PAGE_MAIN else RouteConfig.ROUTE_PAGE_LOGIN) {
            composable(RouteConfig.ROUTE_PAGE_MAIN) {
                MainPage()
            }
            composable(RouteConfig.ROUTE_PAGE_LOGIN) {
                LoginPage(onStateChanged = {logged = true})
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainPage(modifier: Modifier = Modifier) {
        val navController = rememberNavController()
        val items = listOf("Home", "Message", "Profile")
        val icons = listOf(
            Icons.Filled.Home,
            Icons.Filled.Email,
            Icons.Filled.AccountCircle
        )
        var selectedItem by rememberSaveable { mutableIntStateOf(0) }
        Scaffold (
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                navController.navigate(items[selectedItem] + "Page")
                            }
                        )
                    }
                }
            },
            content = { innerPadding ->
                NavHost(
                    navController,
                    startDestination = RouteConfig.ROUTE_PAGE_HOME,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(RouteConfig.ROUTE_PAGE_HOME) {
                        HomePage()
                    }

                    composable(RouteConfig.ROUTE_PAGE_MESSAGE) {
                        MessagePage()
                    }

                    composable(RouteConfig.ROUTE_PAGE_PROFILE) {
                        ProfilePage()
                    }
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun HomePage(modifier: Modifier = Modifier) {
        val keyboardController = LocalSoftwareKeyboardController.current
        var search by rememberSaveable {
            mutableStateOf("")
        }
        var searchStart by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(searchStart) {
            // 在协程中发起网络请求
            if (searchStart) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("search")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(SearchService::class.java)
                val response = withContext(Dispatchers.IO) {
                    apiService.fetchData(search)
                }
                for (newsProfile in response)
                    Log.d("newTitle", newsProfile.title)
                searchStart = false
            }
        }

        Column {
            Surface(
                color = Color(238, 28, 37),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(all = 8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.xuexiqiangguo),
                        contentDescription = "xuexi2",
                        Modifier.height(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = search,
                        onValueChange = {
                            search = it
                        },
                        singleLine = true,
                        label = { Text("習近平　国政運営を語る") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        },
                        modifier = modifier,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done // 将输入法动作设置为“完成”
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                searchStart = true
                                keyboardController?.hide()
                            }
                        )
                    )
                }
            }
            val forums = listOf("会議活動", "接見会見", "考察調研", "出訪",
                "講話全文", "重要文章", "指示批示")
            val navController = rememberNavController()
            Surface(
                color = Color(255, 213, 217),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .height(50.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(forums) { forum ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = forum,
                                style = TextStyle(fontSize = 20.sp,
                                    color = Color(108, 111, 119)),
                                modifier = Modifier
                                    .clickable(onClick = {
                                        navController.navigate(forum)
                                    }),
                                textAlign = TextAlign.Center

                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = "会議活動") {

                composable("会議活動") {
                    ActivityPage()
                }

                composable("接見会見") {
                    MeetingPage()
                }

                composable("考察調研") {
                    SurveyPage()
                }

                composable("出訪") {
                    VisitPage()
                }
                composable("講話全文") {
                    SpeechPage()
                }
                composable("重要文章") {
                    ArticlePage()
                }
                composable("指示批示") {
                    CommandPage()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun HomePagePreview(modifier: Modifier = Modifier) {
        var search by rememberSaveable {
            mutableStateOf("")
        }
        Column {
            Surface(
                color = Color(238, 28, 37),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(all = 8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.xuexiqiangguo),
                        contentDescription = "xuexi2",
                        Modifier.height(50.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = search,
                        onValueChange = {
                            search = it
                        },
                        singleLine = true,
                        label = { Text("習近平　国政運営を語る") },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        },
                        modifier = modifier
                    )
                }
            }
            val forums = listOf("会議活動", "接見会見", "考察調研", "出訪")
            val navController = rememberNavController()
            Surface(
                color = Color(255, 213, 217),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyRow(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .height(50.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(forums) { forum ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = forum,
                                style = TextStyle(fontSize = 20.sp,
                                    color = Color(108, 111, 119)),
                                modifier = Modifier
                                    .clickable(onClick = {
                                        navController.navigate(forum)
                                    }),
                                textAlign = TextAlign.Center

                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            }
            NavHost(
                navController = navController,
                startDestination = "会議活動") {

                composable("会議活動") {
                    ActivityPagePreview()
                }

                composable("接見会見") {
                    MeetingPage()
                }

                composable("考察調研") {
                    SurveyPage()
                }

                composable("出訪") {
                    VisitPage()
                }
            }
        }
    }


    @Composable
    @Preview
    fun ActivityPagePreview(modifier: Modifier = Modifier) {
        val passages = listOf(
            "愛媛県と高知県で震度6弱の地震　夜の地震で気をつけること",
            "茨城県の公園　ネモフィラの花がきれいに咲いている",
            "イランがイスラエルをドローンやミサイルで攻撃した"
        )
        LazyColumn {
            items(passages) {passage ->
                Row {
                    Spacer(modifier = Modifier.width(8.dp))

                    Column() {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = passage,
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

    /*
    @Composable
    fun MeetingPage(modifier: Modifier = Modifier) {
        Text(text = "会見")
    }

    @Composable
    fun SurveyPage(modifier: Modifier = Modifier) {
        Text(text = "考察")
    }

    @Composable
    fun VisitPage(modifier: Modifier = Modifier) {
        Text(text = "出訪")
    }

     */

    @Composable
    fun MessagePage(modifier: Modifier = Modifier) {
        Text(text = "Message")
    }

    @Composable
    fun ProfilePage(modifier: Modifier = Modifier) {
        Text(text = "Profile")
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginPage(onStateChanged: () -> Unit, modifier: Modifier = Modifier) {
        var email by rememberSaveable {
            mutableStateOf("")
        }
        var password by rememberSaveable {
            mutableStateOf("")
        }
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Image(
                painter = painterResource(R.drawable.xuexiqiangguo),
                contentDescription = "xuexi",
                Modifier
            )
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                    },
                    singleLine = true,
                    label = { Text("Enter email") },
                    leadingIcon = {
                        Icon(Icons.Filled.Face, contentDescription = null)
                    },
                    modifier = modifier

                )
                Spacer(Modifier.height(20.dp))
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null)
                    },
                    label = { Text("Enter password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        thread {
                            val retrofit = Retrofit.Builder()
                                .baseUrl("http://10.0.2.2:8080/api/") // Replace with your server's URL
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()
                            val loginService = retrofit.create(LoginService::class.java)
                            val loginRequest = LoginRequest(email, password)
                            val call = loginService.postEmailPassword(loginRequest)

                            call.enqueue(object : Callback<LoginResponse> {
                                override fun onResponse(
                                    call: Call<LoginResponse>,
                                    response: Response<LoginResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.d("Main Activity", "success")
                                        val responseBody = response.body()
                                        if (responseBody != null) {
                                            Log.d("Main Activity", responseBody.id.toString())
                                        }
                                        runOnUiThread {
                                            onStateChanged()
                                        }
                                    } else {
                                        // Handle an error response here
                                        Log.d("Main Activity", "error response")
                                    }
                                }

                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    // Handle network failure here
                                    t.printStackTrace()
                                }
                            })
                        }
                    }
                ) {
                    Text(text = "Login")
                }
            }
        }
    }
}

