package com.example.composeapp

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composeapp.ui.theme.ComposeAppTheme


class NewsDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val url = intent.getStringExtra("url")
        setContent {
            ComposeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (url != null) {
                        WebViewComponent(url = url)
                    }
                }
            }
        }
    }

    @Composable
    fun WebViewComponent(url: String) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                loadUrl(url)
            }
        })
    }
}




/*
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeAppTheme {
        Greeting("Android")
    }
}
 */