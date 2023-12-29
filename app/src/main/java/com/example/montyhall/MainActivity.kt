package com.example.montyhall

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MontyHallApp()
            }
        }
    }
}

@Composable
fun MontyHallApp() {
    var selectedButton by remember { mutableIntStateOf(0) }
    var showConfirmationScreen by remember { mutableStateOf(false) }
    var userIsSure by remember { mutableStateOf(false) }
    var showResultScreen by remember { mutableStateOf(false) }
    var winningButton by remember { mutableIntStateOf(Random.nextInt(1, 4)) }
    Log.d("user is sure", userIsSure.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!showConfirmationScreen && !showResultScreen) {
            // Start screen with 3 buttons
            ButtonRow(
                onClick = { buttonNumber ->
                    selectedButton = buttonNumber
                    Log.d("selected button", selectedButton.toString())
                    showConfirmationScreen = true
                }
            )
        } else if (showConfirmationScreen && !showResultScreen) {
            // Confirmation screen with Yes and No buttons
            ConfirmationScreen(
                onYesClick = {
                    userIsSure = true
                    showConfirmationScreen = false
                    showResultScreen = true
                },
                onNoClick = {
                    showConfirmationScreen = false
                    selectedButton = 0
                }
            )
        } else if (userIsSure) {
            // User is sure, show result screen
            ResultScreen(
                selectedButton = selectedButton,
                winningButton = winningButton,
                onPlayAgainClick = {
                    winningButton = Random.nextInt(1, 4)
                    showConfirmationScreen = false
                    showResultScreen = false
                    userIsSure = false
                    selectedButton = 0
                }
            )
        }
    }
}

@Composable
fun ButtonRow(onClick: (Int) -> Unit) {
    val uriString = "https://en.wikipedia.org/wiki/Monty_Hall"
    val uri = Uri.parse(uriString)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    val context = LocalContext.current
    Text("Monty Hall")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RepeatButton(number = 1, onClick = onClick)
        RepeatButton(number = 2, onClick = onClick)
        RepeatButton(number = 3, onClick = onClick)
    }
    Button(onClick = {
        context.startActivity(intent)
    }) {
        Icon(imageVector = Icons.Default.Info, contentDescription = null)
    }
}

@Composable
fun RepeatButton(number: Int, onClick: (Int) -> Unit) {
    Button(
        onClick = { onClick(number) },
        modifier = Modifier
            .padding(2.dp)
    ) {
        Text("Button $number")
    }
}

@Composable
fun ConfirmationScreen(onYesClick: () -> Unit, onNoClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Are you sure?")
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onYesClick) {
                Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Yes")
            }
            Button(onClick = onNoClick) {
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("No")
            }
        }
    }
}

@Composable
fun ResultScreen(
    selectedButton: Int,
    winningButton: Int,
    onPlayAgainClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selectedButton == winningButton) {
            // User selected the winning button
            Text("Congratulations! You've won!")
            val painter =
                rememberAsyncImagePainter(
                    model = "https://carfans.fr/wp-content/uploads/2023/03/Ferrari_F40_Enzo_8.jpg"
                )
            Image(
                painter = painter,
                contentDescription = "Nice Car",
                modifier = Modifier.size(200.dp).padding(5.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            // User selected the losing button
            Text("Sorry, you've lost. The winning button was $winningButton.")
            val painter =
                rememberAsyncImagePainter(
                    model = "https://images.pexels.com/photos/144240/goat-lamb-little-grass-144240.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"                )
            Image(
                painter = painter,
                contentDescription = "Sad goat",
                modifier = Modifier.size(200.dp).padding(5.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onPlayAgainClick) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Play Again")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MontyHallApp()
}
