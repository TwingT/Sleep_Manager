package com.example.sleepmanager.ui.theme

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleepmanager.App.Companion.prefs
import com.example.sleepmanager.ui.FeelingDialog
import com.example.sleepmanager.ui.RadioButtons
import java.util.Calendar
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun TimetableDetailScreen(
    isSemester: Boolean,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var showScheduleChangeDialog by remember { mutableStateOf(false) }
    var currentCount by remember { mutableStateOf(0) }
    var currentIsSemester by remember { mutableStateOf(false) }
    var showWakeUpTimeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var wakeUpTimeMon = prefs.getString(when(isSemester) {
        true -> "wakeUpMonSemester"
        false -> "wakeUpMonWeek"
    }, "")
    var wakeUpTimeTue = prefs.getString(when(isSemester) {
        true -> "wakeUpTueSemester"
        false -> "wakeUpTueWeek"
    }, "")
    var wakeUpTimeWen = prefs.getString(when(isSemester) {
        true -> "wakeUpWenSemester"
        false -> "wakeUpWenWeek"
    }, "")
    var wakeUpTimeThu = prefs.getString(when(isSemester) {
        true -> "wakeUpThuSemester"
        false -> "wakeUpThuWeek"
    }, "")
    var wakeUpTimeFri = prefs.getString(when(isSemester) {
        true -> "wakeUpFriSemester"
        false -> "wakeUpFriWeek"
    }, "")
    var wakeUpTimeSat = prefs.getString(when(isSemester) {
        true -> "wakeUpSatSemester"
        false -> "wakeUpSatWeek"
    }, "")
    var wakeUpTimeSun = prefs.getString(when(isSemester) {
        true -> "wakeUpSunSemester"
        false -> "wakeUpSunWeek"
    }, "")

    if (showScheduleChangeDialog) {
        scheduleChangeDialog(
            onDismissRequest = { showScheduleChangeDialog = false },
            onOkClick = {
                showScheduleChangeDialog = false
            },
            count = currentCount,
            isSemester = currentIsSemester
        )
    }

    if (showWakeUpTimeDialog) {
        wakeUpTimeDialog(
            onDismissRequest = { showWakeUpTimeDialog = false},
            onOkClick = {showWakeUpTimeDialog = false},
            isSemester = isSemester
        )
    }

    Column() {
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            item { Text(text = "") }
            item { Text(text = "월") }
            item { Text(text = "화") }
            item { Text(text = "수") }
            item { Text(text = "목") }
            item { Text(text = "금") }
            item { Text(text = "토") }
            item { Text(text = "일") }
            item { Button(
                colors = ButtonDefaults.buttonColors(Color.LightGray),
                onClick = { showWakeUpTimeDialog = true }
            ) {
                Text(text = "기상", color = Color.Black)
            }}
            item { Text(text = wakeUpTimeMon)}
            item { Text(text = wakeUpTimeTue)}
            item { Text(text = wakeUpTimeWen)}
            item { Text(text = wakeUpTimeThu)}
            item { Text(text = wakeUpTimeFri)}
            item { Text(text = wakeUpTimeSat)}
            item { Text(text = wakeUpTimeSun)}
            items(200) {
                rowOfTimetable(
                    count = it,
                    isSemester = isSemester,
                    changeCurrentCount = { currentCount = it },
                    changeCurrentIsSemester = { currentIsSemester = it },
                    changeShowScheduleChangeDialog = { showScheduleChangeDialog = it }
                )
            }
        }
        if (isSemester == false) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Button(onClick = {
                    for (i in 0..199) {
                        prefs.setBoolean("$i isBlack", false)
                        prefs.setString("$i week",prefs.getString("$i semester",""))
                    }
                    prefs.setString("sleepMonWeek","없음")
                    prefs.setString("sleepTueWeek","없음")
                    prefs.setString("sleepWenWeek","없음")
                    prefs.setString("sleepThuWeek","없음")
                    prefs.setString("sleepFriWeek","없음")
                    prefs.setString("sleepSatWeek","없음")
                    prefs.setString("sleepSunWeek","없음")
                    prefs.setString("wakeUpMonWeek",prefs.getString("wakeUpMonSemester",""))
                    prefs.setString("wakeUpTueWeek",prefs.getString("wakeUpTueSemester",""))
                    prefs.setString("wakeUpWenWeek",prefs.getString("wakeUpWenSemester",""))
                    prefs.setString("wakeUpThuWeek",prefs.getString("wakeUpThuSemester",""))
                    prefs.setString("wakeUpFriWeek",prefs.getString("wakeUpFriSemester",""))
                    prefs.setString("wakeUpSatWeek",prefs.getString("wakeUpSatSemester",""))
                    prefs.setString("wakeUpSunWeek",prefs.getString("wakeUpSunSemester",""))
                }) {
                    Text(text = "시간표 초기화")
                }
                Button(onClick = {
                    recommendedSleepTime(Day = "Mon", isSemester = isSemester)
                    recommendedSleepTime(Day = "Tue", isSemester = isSemester)
                    recommendedSleepTime(Day = "Wen", isSemester = isSemester)
                    recommendedSleepTime(Day = "Thu", isSemester = isSemester)
                    recommendedSleepTime(Day = "Fri", isSemester = isSemester)
                    recommendedSleepTime(Day = "Sat", isSemester = isSemester)
                    recommendedSleepTime(Day = "Sun", isSemester = isSemester)
                    setIsBlack(Day = "Mon")
                    setIsBlack(Day = "Tue")
                    setIsBlack(Day = "Wen")
                    setIsBlack(Day = "Thu")
                    setIsBlack(Day = "Fri")
                    setIsBlack(Day = "Sat")
                    setIsBlack(Day = "Sun")
                }) {
                    Text(text = "처리")
                }
                Button(onClick = {
                    for (i in 0..199) {
                        prefs.setBoolean("$i isBlack", false)
                    }
                    prefs.setString("sleepMonWeek","없음")
                    prefs.setString("sleepTueWeek","없음")
                    prefs.setString("sleepWenWeek","없음")
                    prefs.setString("sleepThuWeek","없음")
                    prefs.setString("sleepFriWeek","없음")
                    prefs.setString("sleepSatWeek","없음")
                    prefs.setString("sleepSunWeek","없음")
                }) {
                    Text(text = "취소")
                }
            }
        }
    }
}

@Composable
private fun rowOfTimetable(
    count: Int,
    isSemester: Boolean,
    changeCurrentCount: (Int) -> Unit,
    changeCurrentIsSemester: (Boolean) -> Unit,
    changeShowScheduleChangeDialog: (Boolean) -> Unit
) {
    val schedule = when (isSemester) {
        true -> prefs.getString("$count semester", "")
        false -> prefs.getString("$count week", "")
    }
    val timeList = listOf("0:00~1:00","1:00~2:00","2:00~3:00","3:00~4:00","4:00~5:00","5:00~6:00",
        "6:00~7:00","7:00~8:00","8:00~8:50","8:50~9:50","9:50~10:50","10:50~11:50","11:50~12:40",
        "12:40~1:40","13:40~14:40","14:40~15:40","15:40~16:40","16:40~17:40","17:40~18:30",
        "18:30~19:30","19:30~20:30","20:30~21:30","21:30~22:00","22:00~23:00","23:00~24:00")

    //prefs.setBoolean("$count isBlack", false)
    if (prefs.getBoolean("$count isBlack",false)) {
        Button(
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(Color.Black),
            onClick = {}) {}
    } else {

        if (count % 8 == 0) {
            Column(
                verticalArrangement = Arrangement.Center
            ) { Text(text = timeList[count / 8], fontSize = 13.sp) }
        } else Button(
            shape = RectangleShape,
            colors = when (schedule == "") {
                false -> ButtonDefaults.buttonColors(Color.White)
                true -> ButtonDefaults.buttonColors(Color.Gray)
            },
            onClick = {
                changeCurrentCount(count)
                changeCurrentIsSemester(isSemester)
                changeShowScheduleChangeDialog(true)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) { Text(text = schedule, color = Color.Black, fontSize = 13.sp) }
        }
    }
    }

@Composable
fun scheduleChangeDialog(
    onDismissRequest: () -> Unit,
    onOkClick: (String) -> Unit,
    count: Int,
    isSemester: Boolean
) {
    var scheduleValue by remember {
        mutableStateOf(prefs.getString(when (isSemester) {
                                                         true -> "$count semester"
                                                        false -> "$count week"
                                                         },""))
    }
    AlertDialog(
        title = {Text(text = "스케줄 입력")},
        text = { EditStringField(
            label = "hello",
            value = scheduleValue,
            onValueChange = {scheduleValue = it}) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onOkClick(scheduleValue)
                when (isSemester) {
                    true -> {prefs.setString("$count semester",scheduleValue)
                        prefs.setString("$count week", scheduleValue)}
                    false -> prefs.setString("$count week", scheduleValue)
                }
            }) {
                Text(text = "확인")
            }
        }
    )
}

@Composable
fun wakeUpTimeDialog(
    onDismissRequest: () -> Unit,
    onOkClick: (String) -> Unit,
    isSemester: Boolean
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var wakeUpTimeMon by remember { mutableStateOf("월") }
    var wakeUpTimeTue by remember { mutableStateOf("화") }
    var wakeUpTimeWen by remember { mutableStateOf("수") }
    var wakeUpTimeThu by remember { mutableStateOf("목") }
    var wakeUpTimeFri by remember { mutableStateOf("금") }
    var wakeUpTimeSat by remember { mutableStateOf("토") }
    var wakeUpTimeSun by remember { mutableStateOf("일") }

    val timePickerDialog1 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            wakeUpTimeMon = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog2 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            wakeUpTimeTue = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog3 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            wakeUpTimeWen = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog4 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            wakeUpTimeThu = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog5 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            wakeUpTimeFri = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog6 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            wakeUpTimeSat = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog7 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            wakeUpTimeSun = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )
    AlertDialog(
        title = {Text(text = "기상 시간 선택")},
        text = { 
               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.SpaceAround) {
                   Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                   verticalAlignment = Alignment.CenterVertically) {
                       Button(onClick = { timePickerDialog1.show() }) {
                           Text(text = wakeUpTimeMon)
                       }
                       Button(onClick = { timePickerDialog2.show() }) {
                           Text(text = wakeUpTimeTue)
                       }
                   }
                   Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                       verticalAlignment = Alignment.CenterVertically) {
                       Button(onClick = { timePickerDialog3.show() }) {
                           Text(text = wakeUpTimeWen)
                       }
                       Button(onClick = { timePickerDialog4.show() }) {
                           Text(text = wakeUpTimeThu)
                       }
                   }
                   Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                   verticalAlignment = Alignment.CenterVertically) {
                       Button(onClick = { timePickerDialog5.show() }) {
                           Text(text = wakeUpTimeFri)
                       }
                       Button(onClick = { timePickerDialog6.show() }) {
                           Text(text = wakeUpTimeSat)
                       }
                   }
                   Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                       verticalAlignment = Alignment.CenterVertically) {
                       Button(onClick = { timePickerDialog7.show() }) {
                           Text(text = wakeUpTimeSun)
                       }
                   }
               }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                prefs.setString("wakeUpMonWeek",wakeUpTimeMon)
                prefs.setString("wakeUpTueWeek",wakeUpTimeTue)
                prefs.setString("wakeUpWenWeek",wakeUpTimeWen)
                prefs.setString("wakeUpThuWeek",wakeUpTimeThu)
                prefs.setString("wakeUpFriWeek",wakeUpTimeFri)
                prefs.setString("wakeUpSatWeek",wakeUpTimeSat)
                prefs.setString("wakeUpSunWeek",wakeUpTimeSun)
                if (isSemester) {
                    prefs.setString("wakeUpMonSemester",wakeUpTimeMon)
                    prefs.setString("wakeUpTueSemester",wakeUpTimeTue)
                    prefs.setString("wakeUpWenSemester",wakeUpTimeWen)
                    prefs.setString("wakeUpThuSemester",wakeUpTimeThu)
                    prefs.setString("wakeUpFriSemester",wakeUpTimeFri)
                    prefs.setString("wakeUpSatSemester",wakeUpTimeSat)
                    prefs.setString("wakeUpSunSemester",wakeUpTimeSun)
                } else {}
                onOkClick("")
            }) {
                Text(text = "확인")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStringField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label)},
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

private fun getButtonIndex(
    Day: String,
    Time: String
):Int {
    val x = when (Day) {
        "Mon" -> 0
        "Tue" -> 1
        "Wen" -> 2
        "Thu" -> 3
        "Fri" -> 4
        "Sat" -> 5
        "Sun" -> 6
        else -> -1
    }
    val timeSplitList = Time.split(":")
    val timeDouble = timeSplitList[0].toInt()*60 + timeSplitList[1].toInt()

    val y = when(-timeDouble) {
        in -60 until 0 -> 0
        in -120 until -60 -> 1
        in -180 until -120 -> 2
        in -240 until -180 -> 3
        in -300 until -240 -> 4
        in -360 until -300 -> 5
        in -420 until -360 -> 6
        in -480 until -420 -> 7
        in -530 until -480 -> 8
        in -590 until -530 -> 9
        in -650 until -590 -> 10
        in -710 until -650 -> 11
        in -760 until -710 -> 12
        in -820 until -760 -> 13
        in -880 until -820 -> 14
        in -940 until -880 -> 15
        in -1000 until -940 -> 16
        in -1060 until -1000 -> 17
        in -1110 until -1060 -> 18
        in -1170 until -1110 -> 19
        in -1230 until -1170 -> 20
        in -1290 until -1230 -> 21
        in -1320 until -1290 -> 22
        in -1380 until -1320 -> 23
        in -1440 until -1380 -> 24
        else -> -1
    }

    return 8*y + x+1
}

private fun getPreviousIndex(n: Int):Int {
    if (n>=8) {
        return n-8
    } else {
        return n+191
    }
}

private fun getNextIndex(n: Int): Int {
    if (n<=191) {
        return  n+8
    } else {
        return n-191
    }
}

private fun getStartTimeOfButton(
    Index: Int
): String {
    val timeIndex = Index - Index%8
    val startTimeList = listOf("0:00","1:00","2:00","3:00","4:00","5:00",
        "6:00","7:00","8:00","8:50","9:50","10:50","11:50",
        "12:40","13:40","14:40","15:40","16:40","17:40",
        "18:30","19:30","20:30","21:30","22:00","23:00")
    return startTimeList[timeIndex/8]
}

private fun getLastStartTime(
    Day: String,
    isSemester: Boolean
):String {
    val wakeUpIndex = getButtonIndex(Day = Day,
        Time = prefs.getString(when(isSemester) {
            true -> "wakeUp${Day}Semester"
            false -> "wakeUp${Day}Week" },""))
    var count = 0
    var currentIndex = wakeUpIndex
    var nextIndex = getPreviousIndex(currentIndex)

    while (count<9 && prefs.getString(when(isSemester) {
        true -> "$nextIndex semester"
            false -> "$nextIndex week"
    }, "")=="" && currentIndex != 1) {
        count += 1
        currentIndex = nextIndex
        nextIndex = getPreviousIndex(nextIndex)
    }
    return getStartTimeOfButton(currentIndex)
}

private fun recommendedSleepTime(
    Day: String,
    isSemester: Boolean
): String {
    val wakeUpTimeSplitList = prefs.getString(when(isSemester) {
        true -> "wakeUp${Day}Semester"
        false -> "wakeUp${Day}Week"
    },"").split(":")
    val wakeUpTime = wakeUpTimeSplitList[0].toInt()*60 + wakeUpTimeSplitList[1].toInt()
    val lastStartTimeSplitList = getLastStartTime(Day = Day, isSemester = isSemester).split(":")
    val lastStartTime = lastStartTimeSplitList[0].toInt()*60 + lastStartTimeSplitList[1].toInt()

    val totalLength = (wakeUpTime - lastStartTime + 1440)%1440 // minute

    var sleepLength: Int = totalLength

    while (isRecommendedTime(sleepLength) == false) {
        sleepLength -= 1
    }

    val recommendedSleepTime = (wakeUpTime - sleepLength +1440)%1440
    val recommendedSleepTimeString = (recommendedSleepTime/60).toString() + ":" + (recommendedSleepTime%60).toString()
    prefs.setString(when(isSemester) {
        true -> "sleep${Day}Semester"
        false -> "sleep${Day}Week"
    },recommendedSleepTimeString)
    return recommendedSleepTimeString
}

private fun isRecommendedTime(
    length: Int
): Boolean {
    val numberOfRem = prefs.getInt("numberOfRem",0)
    var isRecommended: Boolean = false

    if (length <= 240) {
        return true
    }

    for (i in 0 until numberOfRem) {
        if ((prefs.getString("$i remStart","").toDouble()+0.1 <= length.toDouble()/60)
            && (length.toDouble()/60 <= prefs.getString("$i remEnd","").toDouble()-0.1)) {
            isRecommended = true
        }
    }
    return isRecommended
}

fun setIsBlack(
    Day: String
) {
    val wakeUpIndex = getButtonIndex(Day = Day,
        Time = prefs.getString("wakeUp${Day}Week",""))
    var currentIndex = wakeUpIndex
    val sleepTime = prefs.getString("sleep${Day}Week","")
    val sleepIndexCandidate = listOf(getButtonIndex(Day = "Mon",Time = sleepTime),
        getButtonIndex(Day = "Tue",Time = sleepTime),getButtonIndex(Day = "Wen",Time = sleepTime),
        getButtonIndex(Day = "Thu",Time = sleepTime),getButtonIndex(Day = "Fri",Time = sleepTime),
        getButtonIndex(Day = "Sat",Time = sleepTime),getButtonIndex(Day = "Sun",Time = sleepTime))
    var stop: Boolean = false
    while (!stop && (prefs.getString("$currentIndex week","")=="")) {
            prefs.setBoolean("$currentIndex isBlack",true)
            if (currentIndex==1) {
                stop = true
            }
            if (currentIndex in sleepIndexCandidate) {
                stop = true
            }

            currentIndex = getPreviousIndex(currentIndex)
        }
    }

/*
@Preview
@Composable
fun TimetableDetailScreenPreview() {
    TimetableDetailScreen(mutableListOf("0","a","b","c","d","e","f","g","0","a","b","c","d","e","f",
        "g","0","a","b","c","d","e","f","g","0","a","b","c","","e","f","g","0","a","b","c","d","e",
        "f","g","0","a","b","c","d","e","f","g","0","a","b","c","d","e","f","g","0","a","b","c","d",
        "e","f","g","0","a","b","c","d","e","f","g","0","a","b","c","d","e","f","g","0","a","b","c",
        "d","e","f","g","0","a","b","c","d","e","f","g","0","a","b","c","d","e","f","g","0","a","b",
        "c","d","e","f","g","0","a","b","c","d","e","f","g","0","a","b","c","d","e","f","g","0","a",
        "b","c","d","e","f","g","0","a","b","c","d","e","f","g","0","a","b","c","d","e","f","g","0",
        "a","b","c","d","e","f","g","0","a","b","c","d","e","f","g","0","a","b","c","d","e","f","g",
        "0","a","b","c","d","e","f","g","0","a","b","c","d","e","f","g","0","a","b","c","d","e","f",
        "g"), true)
}*/