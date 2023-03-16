package com.example.android.roomyweather.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.example.android.roomyweather.data.Forecast
import com.example.android.roomyweather.data.SkiResort
import com.example.android.roomyweather.data.WeatherAlert
import com.ortiz.touchview.TouchImageView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val forecastViewModel by viewModels<ForecastViewModel>()
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        forecastViewModel.loadCurrentWeatherReport()
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        setContent {
            MainNav(forecastViewModel, sharedPrefs)
        }

    }
}

@Composable
fun MainNav(forecastViewModel: ForecastViewModel, sharedPrefs: SharedPreferences) {
    val navController = rememberNavController()
    val resorts: List<SkiResort> by forecastViewModel.resorts.observeAsState(listOf())
    val loaded: Boolean by forecastViewModel.loaded.observeAsState(false)
    when (loaded) {
        false -> Column() {
            topAppBar(navController, title = "Ski Resorts")
            Text(fontSize = 30.sp, text = "Loading...")
        }
        true -> Column() {

            NavHost(
                navController = navController,
                startDestination = "main",
            ) {
                composable("main") {
                    Column(
                        Modifier.background(Color(0xffd9f0fa))
                    ) {
                        topAppBar(navController, title = "Ski Resorts")
                        LazyColumn() {
                            items(resorts) { resort ->
                                ResortCard(
                                    navController,
                                    resort.name,
                                    resort.forecast.firstOrNull()?.temp,
                                    resort.snowfall
                                )
                            }
                        }
                    }
                }
                composable("settings") {
                    Column() {
                        topAppBar(navController = navController, title = "Settings")
                        HourlyForecastLimitSlider(sharedPrefs)
                    }
                }
                for (resort in resorts) {
                    composable(resort.name) {
                        Column {
                            topAppBar(navController = navController, title = resort.name)
                            ResortDetailPage(navController, sharedPrefs, resort)
                        }
                    }
                    composable(resort.name + " Trail Map") {
                        Column {
                            topAppBar(navController = navController, title = resort.name + " Trail Map")
                            ZoomableImage(resourceId = resort.trailMapResID)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun topAppBar(navController: NavController, title: String) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            if (title != "Settings") {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Filled.Settings, null)
                }
            }
        }
    )
}

@Composable
fun HourlyForecastLimitSlider(sharedPrefs: SharedPreferences) {
    var sliderStartPosition = sharedPrefs.getInt("hourlyForecastLimit", 8)
    var sliderPosition by remember { mutableStateOf(sliderStartPosition.toFloat()) }
    Column(
        Modifier.padding(30.dp)
    ) {
        Text(
            text = "Hourly Forecast Limit",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text(
            text = sliderPosition.roundToInt().toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                sharedPrefs.edit().putInt("hourlyForecastLimit", sliderPosition.roundToInt()).apply()
            },
            valueRange = 8f..48f,
            steps = 39
        )
    }

}

@Composable
fun ResortCard(navController: NavController, name: String, temp: Int?, snowfall: String) {
    Card(
        Modifier
            .padding(5.dp)
            .clickable(onClick = { navController.navigate(name) })
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .padding(10.dp)
        ) {
            Text(
                text = name,
                fontSize = 30.sp
            )
            Row(
                Modifier
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        Icon(
                            Icons.Filled.Thermostat,
                            "temperature icon",
                            modifier = Modifier
                                .size(32.dp),
                            tint = Color.Red
                        )
                        Text(
                            text = "$temp°F",
                            fontSize = 24.sp
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        Icon(
                            Icons.Filled.AcUnit,
                            "snowflake icon",
                            modifier = Modifier
                                .size(32.dp),
                            tint = Color.Blue
                        )
                        Text(
                            text = snowfall + " in",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResortDetailPage(navController: NavController, sharedPrefs: SharedPreferences, resort: SkiResort) {
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(Color(0xffd9f0fa))
    ) {
        Column(
            Modifier.padding(10.dp)
        ) {
            Text(
                text = "Snowfall in the last 24 hours:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = resort.snowfall + " inches",
                fontSize = 20.sp
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                navController.navigate(resort.name + " Trail Map")
            }) {
                Text("Trail Map")
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                val websiteURI = Uri.parse(resort.website)
                val webIntent = Intent(Intent.ACTION_VIEW, websiteURI)
                startActivity(context, webIntent, null)
            }) {
                Text("Resort Website")
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                val lat = resort.latitude
                val long = resort.longitude
                val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$long")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(context, mapIntent, null)
            }) {
                Text("Directions to Resort")
            }
        }
        Column(
            Modifier.padding(10.dp)
        ) {
            Text(
                text = "Weather Forecast:",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            resort.alerts.forEach {
                AlertCard(it)
            }
            var hourlyForecastLimit = sharedPrefs.getInt("hourlyForecastLimit", 8)
            resort.forecast.take(hourlyForecastLimit).forEach {
                ForecastCard(it)
            }
        }
    }
}

@Composable
fun ForecastCard(forecast: Forecast) {
    Card(
        Modifier
            .padding(0.dp, 4.dp, 0.dp, 4.dp)
            .fillMaxWidth()
    ) {
        Column(
            Modifier.padding(10.dp)
        ) {
            Row() {
                Text(
                    text = LocalDateTime.ofEpochSecond(
                        forecast.epoch.toLong(),
                        0,
                        ZoneId.systemDefault().rules.getOffset(Instant.now())
                    )
                        .format(DateTimeFormatter.ofPattern("MMM d h:mm a")),
                    fontWeight = FontWeight.Bold
                )
            }
            Row() {
                Column(Modifier.fillMaxWidth(0.2f)) {
                    Text(
                        text = "${forecast.temp}°"
                    )
                }
                Column(Modifier.fillMaxWidth(0.65f)) {
                    androidx.compose.material.Text(
                        text = "Wind speed: ${forecast.windSpeed}mph"
                    )
                }
                Column {
                    Text(
                        text = forecast.description
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun AlertCard(alert: WeatherAlert) {
    Card(
        Modifier
            .padding(0.dp, 4.dp, 0.dp, 4.dp)
            .fillMaxWidth(),
        backgroundColor = Color(0xfffa9696)
    ) {
        Column(
            Modifier.padding(10.dp)
        ) {
            Row() {
                Icon(
                    Icons.Filled.Warning,
                    "alert icon",
                    modifier = Modifier
                        .size(58.dp)
                        .padding(0.dp, 0.dp, 5.dp, 0.dp),
                    tint = Color.Red
                )
                Text(
                    text = alert.headline,
                    fontWeight = FontWeight.Bold
                )
            }
            Row() {
                Text(text = alert.area)
            }
            Row() {
                Text(text = alert.description)
            }
        }
    }
}

@Composable
fun ZoomableImage(resourceId: Int) {
    AndroidView(factory = { ctx ->
        TouchImageView(ctx).apply {
            setImageResource(resourceId)
            setMaxZoomRatio(10f)
        }
    })
}
