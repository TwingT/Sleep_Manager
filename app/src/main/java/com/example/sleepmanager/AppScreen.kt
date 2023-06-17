package com.example.sleepmanager

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sleepmanager.App.Companion.prefs
import com.example.sleepmanager.data.DataSource.menus
import com.example.sleepmanager.data.DataSource.temporaryArray
import com.example.sleepmanager.data.DataSource.temporaryRecord
import com.example.sleepmanager.ui.AlarmScreen
import com.example.sleepmanager.ui.CyCleControlFirstScreen
import com.example.sleepmanager.ui.CycleControlSecondScreen
import com.example.sleepmanager.ui.HowToUseScreen
import com.example.sleepmanager.ui.SleepAnalysis
import com.example.sleepmanager.ui.SleepRecordScreen
import com.example.sleepmanager.ui.theme.StartScreen
import com.example.sleepmanager.ui.theme.TimetableDetailScreen
import com.example.sleepmanager.ui.theme.TimetableScreen
import kotlin.math.abs

enum class AppScreen(@StringRes val title: Int) {
    Main(title = R.string.app_name),
    Record(title = R.string.sleepRecord),
    Analysis(title = R.string.sleepAnalysis),
    Timetable(title = R.string.timetable),
    SemesterTimetable(title = R.string.semester_timetable),
    WeekTimetable(title = R.string.week_timetable),
    CycleFirst(title = R.string.cycleControl),
    CycleSecond(title = R.string.cycleControl),
    Alarm(title = R.string.alarm),
    HowToUse(title = R.string.how_to_use)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Main.name
    )

    Scaffold(
        topBar = {
            AppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AppScreen.Main.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.Main.name) {
                StartScreen(
                    menus = menus,
                    onButtonClicked = {
                        when(it) {
                            R.string.sleepRecord -> navController.navigate(AppScreen.Record.name)
                            R.string.sleepAnalysis -> navController.navigate(AppScreen.Analysis.name)
                            R.string.timetable -> navController.navigate(AppScreen.Timetable.name)
                            R.string.cycleControl -> if (prefs.getBoolean("beforeCycleControl",true)) {navController.navigate(AppScreen.CycleFirst.name)}
                            else {navController.navigate(AppScreen.CycleSecond.name)}
                            R.string.alarm -> navController.navigate(AppScreen.Alarm.name)
                            R.string.how_to_use -> navController.navigate(AppScreen.HowToUse.name)
                            // sometimes cycleControl -> CycleSecond... We should add it.
                        }
                    }
                )
            }
            composable(route = AppScreen.Record.name) {
                SleepRecordScreen()
            }
            composable(route = AppScreen.Analysis.name) {
                SleepAnalysis(
                    sleepStageInformation = listForSleepAnalysis()
                )
            }
            composable(route = AppScreen.Timetable.name) {
                TimetableScreen(
                    onButtonClicked = {
                        when(it) {
                            1 -> navController.navigate(AppScreen.SemesterTimetable.name)
                            2 -> navController.navigate(AppScreen.WeekTimetable.name)
                        }
                    }
                )
            }
            composable(route = AppScreen.SemesterTimetable.name) {
                TimetableDetailScreen(
                    isSemester = true
                )
            }
            composable(route = AppScreen.WeekTimetable.name) {
                TimetableDetailScreen(
                    isSemester = false
                )
            }
            composable(route = AppScreen.CycleFirst.name) {
                CyCleControlFirstScreen()
            }
            composable(route = AppScreen.CycleSecond.name) {
                CycleControlSecondScreen()
            }
            composable(route = AppScreen.Alarm.name) {
                AlarmScreen(

                )
            }
            composable(route = AppScreen.HowToUse.name) {
                HowToUseScreen(

                )
            }
        }

    }
}

fun listForSleepAnalysis():List<Double> {
    var maxAccuracy = 0
    var maxState = listOf(0.0,0.0,0.0)

    if (prefs.getInt("numberOfRecord",0) == 0) {
        return listOf(0.0,0.0,0.0)
    }

    for (initialTime in 10..30) {
        for (lengthOfNonRem in 40..90) {
            for (lengthOfRem in 30..50) {
                var accuracy: Int = 0
                for (i in 0 until prefs.getInt("numberOfRecord",0)) {
                    val feeling = prefs.getString("$i feeling","")
                    val expectedFeeling = expectedFeeling(i,initialTime,lengthOfNonRem,lengthOfRem)
                    if ((feeling == "보통") || (expectedFeeling == "보통")) {
                        accuracy += 1
                    } else if (((feeling == "나쁨") && (expectedFeeling == "나쁨")) || ((feeling == "좋음") && (expectedFeeling == "좋음"))) {
                        accuracy += 2
                    }
                }
                if (accuracy > maxAccuracy) {
                    maxAccuracy = accuracy
                    maxState = listOf(initialTime.toDouble()/60, lengthOfNonRem.toDouble()/60, lengthOfRem.toDouble()/60)
                }
            }
        }
    }
    return maxState
}

fun expectedFeeling(i: Int, initialTime: Int, lengthOfNonRem: Int, lengthOfRem: Int):String {
    val sleepTimeSplitList = prefs.getString("$i sleepTime", "").split(":")
    val wakeUpTimeSplitList = prefs.getString("$i wakeUpTime", "").split(":")
    val sleepTime = sleepTimeSplitList[0].toDouble() + sleepTimeSplitList[1].toDouble() * (2/3)
    val wakeUpTime = wakeUpTimeSplitList[0].toDouble() + wakeUpTimeSplitList[1].toDouble() * (2/3)
    val sleepLength = (wakeUpTime - sleepTime)%24

    if (sleepLength < initialTime.toDouble()/60) {return "보통"}

    var sum = initialTime.toDouble()/60
    var isRem = false
    while (sum <= sleepLength) {
        if (isRem) {
            sum += lengthOfRem.toDouble()/60
        } else {
            sum += lengthOfNonRem.toDouble()/60
        }
        if (sum > sleepLength) {
            return when(isRem) {
                true -> "좋음"
                false -> "나쁨"
            }
        }
        isRem = !isRem
    }
    return "this cannot be returned"
}

