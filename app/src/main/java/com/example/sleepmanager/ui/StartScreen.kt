package com.example.sleepmanager.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleepmanager.data.DataSource.menus
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleepmanager.App
import com.example.sleepmanager.App.Companion.prefs
import com.example.sleepmanager.ui.onAddButtonClicked
import com.example.sleepmanager.ui.sortRecord
import java.util.Calendar
import kotlin.math.abs

@Composable
fun StartScreen(
    menus: List<Pair<Int,Int>>,
    onButtonClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var sleeping by remember { mutableStateOf(prefs.getBoolean("sleeping",false)) }
    var feelingStateToAdd by remember{ mutableStateOf("보통") }
    var showAlertDialog by remember { mutableStateOf(false) }
    var currentSleepingState by remember { mutableStateOf("현재 상태 : 기상") }

    if (showAlertDialog) {
        FeelingDialog(
            onDismissRequest = {showAlertDialog = false},
            onOkClick = {
                showAlertDialog = false
                feelingStateToAdd = it
                onAddButtonClicked(
                    dateStateToAdd = prefs.getString("sleepDateBySwitch","0년 0월 0일"),
                    timeStateSleepTimeToAdd = prefs.getString("sleepTimeBySwitch","0:0"),
                    timeStateWakeUpTimeToAdd = prefs.getString("wakeUpTimeBySwitch","0:0"),
                    feelingStateToAdd = feelingStateToAdd
                )
                sortRecord()
            }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sleep Manager",
            fontSize = 45.sp
        )
        Spacer(modifier = modifier.height(50.dp))
        Text(text = "오늘 잘 시간은?", fontSize = 23.sp)
        Spacer(modifier = Modifier.height(15.dp))
        CalculateNextSleepAndWakeUp()
        Text(
            text = prefs.getString("tomorrowTimeToSleep","") + " ~ " + prefs.getString("tomorrowTimeToWakeUp",""),
            fontSize = 30.sp
        )
        Spacer(modifier = modifier.height(50.dp))

        Text(text = currentSleepingState)
        Switch(
            checked = sleeping,
            onCheckedChange = {
                sleeping = it
                if (it) {
                    prefs.setBoolean("sleeping",true)
                    prefs.setString("sleepTimeBySwitch", getCurrentTime())
                    prefs.setString("sleepDateBySwitch", getCurrentDate())
                    currentSleepingState = "현재 상태 : 수면 중"
                } else {
                    currentSleepingState = "현재 상태 : 기상"
                    prefs.setBoolean("sleeping",false)
                    prefs.setString("wakeUpTimeBySwitch", getCurrentTime())
                    showAlertDialog = true


                }
            }
        )

        Spacer(modifier = modifier.height(50.dp))
        menus.forEach { item ->
            Row(
                modifier = modifier
            ) {
                SelectMenuButton(item.first, onButtonClicked)
                Spacer(modifier = Modifier.width(30.dp))
                SelectMenuButton(item.second, onButtonClicked)
            }
            Spacer(modifier = modifier.height(10.dp))
        }
        Spacer(modifier = modifier.height(100.dp))
    }
}

@Composable
fun SelectMenuButton(
    menuName: Int,
    onButtonClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onButtonClicked(menuName) },
        modifier = modifier.width(120.dp)
    ) {
        Text(stringResource(id = menuName))
    }
}

fun CalculateNextSleepAndWakeUp() {
    if (prefs.getBoolean("beforeCycleControl",true)) {
        val todayDay = doDayOfWeek()
        val tomorrowDay = when(todayDay) {
            "월" -> "Tue"
            "화" -> "Wen"
            "수" -> "Thu"
            "목" -> "Fri"
            "금" -> "Sat"
            "토" -> "Sun"
            "일" -> "Mon"
            else -> ""
        }

        prefs.setString("tomorrowTimeToSleep",prefs.getString("sleep${tomorrowDay}Week","없음"))
        prefs.setString("tomorrowTimeToWakeUp",prefs.getString("wakeUp${tomorrowDay}Week","없음"))


    } else {
        val n = prefs.getInt("numberOfRecord",0)
        val goalTimeToSleepSplitList = prefs.getString("goalTimeToSleep", "").split(":")
        val goalTimeToSleep = goalTimeToSleepSplitList[0].toDouble() + goalTimeToSleepSplitList[1].toDouble()/60
        val currentTimeToSleepSplitList = prefs.getString("${n-1} sleepTime","").split(":")
        val currentTimeToSleep = currentTimeToSleepSplitList[0].toDouble() + currentTimeToSleepSplitList[1].toDouble()/60
        val goalTimeToWakeUpSplitList = prefs.getString("goalTimeToWakeUp", "").split(":")
        val goalTimeToWakeUp = goalTimeToWakeUpSplitList[0].toDouble() + goalTimeToWakeUpSplitList[1].toDouble()/60
        val currentTimeToWakeUpSplitList = prefs.getString("${n-1} wakeUpTime","").split(":")
        val currentTimeToWakeUp = currentTimeToWakeUpSplitList[0].toDouble() + currentTimeToWakeUpSplitList[1].toDouble()/60

        prefs.setInt("cycleControlMethod",when(abs(goalTimeToWakeUp - currentTimeToWakeUp+24) %24 <= 4) {
            true -> 1
            false -> 2
        })
        val cycleControlMethod = prefs.getInt("cycleControlMethod",1)

        if (cycleControlMethod == 2 && prefs.getString("goalDay","0").toInt()<=0) {
            prefs.setBoolean("beforeCycleControl", true)
        } else if ((currentTimeToSleep-goalTimeToSleep+24)%24 < 1/3 || (currentTimeToSleep-goalTimeToSleep+24)%24 > 23+2/3) {
            prefs.setBoolean("beforeCycleControl", true)
        } else if (cycleControlMethod == 1) {
            prefs.setString("tomorrowTimeToWakeUp", prefs.getString("goalTimeToWakeUp", ""))
            val sign: Double = when (((goalTimeToSleep-currentTimeToSleep+24)%24) < 12) {
                true -> 1.0
                false -> -1.0
            }  // true means goal is later than current.
            val tomorrowTimeToSleep = (currentTimeToSleep + (0.25 * sign)+24)%24
            prefs.setString("tomorrowTimeToSleep", "${tomorrowTimeToSleep.toInt()} : ${(tomorrowTimeToSleep%1*60).toInt()}" )
            prefs.setString("goalDay", (prefs.getString("goalDay","0").toInt()-1).toString())
        } else {
            val sleepLength = (goalTimeToWakeUp-goalTimeToSleep+24)%24
            val difference = (goalTimeToSleep-currentTimeToSleep+24)%24
            val goalDay = prefs.getString("goalDay","").toInt()
            val tomorrowTimeToSleep = (currentTimeToSleep + difference/goalDay+24)%24
            val tomorrowTimeToWakeUp = (tomorrowTimeToSleep + sleepLength+24)%24
            prefs.setString("tomorrowTimeToSleep","${tomorrowTimeToSleep.toInt()} : ${(tomorrowTimeToSleep%1*60).toInt()}")
            prefs.setString("tomorrowTimeToWakeUp", "${tomorrowTimeToWakeUp.toInt()} : ${(tomorrowTimeToWakeUp%1*60).toInt()}")
        }


    }
}

private fun doDayOfWeek(): String? {
    val cal: Calendar = Calendar.getInstance()
    var strWeek: String? = null
    val nWeek: Int = cal.get(Calendar.DAY_OF_WEEK)

    if (nWeek == 1) {
        strWeek = "일"
    } else if (nWeek == 2) {
        strWeek = "월"
    } else if (nWeek == 3) {
        strWeek = "화"
    } else if (nWeek == 4) {
        strWeek = "수"
    } else if (nWeek == 5) {
        strWeek = "목"
    } else if (nWeek == 6) {
        strWeek = "금"
    } else if (nWeek == 7) {
        strWeek = "토"
    }
    return strWeek
}

private fun getCurrentTime(): String {
    val cal: Calendar = Calendar.getInstance()
    val hour: Int = cal.get(Calendar.HOUR_OF_DAY)
    val minute: Int = cal.get(Calendar.MINUTE)

    return "$hour:$minute"
}

private fun getCurrentDate(): String {
    val cal: Calendar = Calendar.getInstance()
    val year: Int = cal.get(Calendar.YEAR)
    val month: Int = cal.get(Calendar.MONTH)+1
    val date: Int = cal.get(Calendar.DAY_OF_MONTH)

    return "${year}년 ${month}월 ${date}일"
}

@Composable
fun FeelingDialog(
    onDismissRequest: () -> Unit,
    onOkClick: (String) -> Unit
) {
    var selectedValue by remember {
        mutableStateOf("")
    }
    AlertDialog(
        title = {Text(text = "기상 후 컨디션")},
        text = {RadioButtons({selectedValue = it}, listOf("나쁨", "보통", "좋음"))},
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {onOkClick(selectedValue)}) {
                Text(text = "확인")
            }
        }
    )
}

@Composable
fun RadioButtons(
    onValueSelect: (String) -> Unit,
    listForButton: List<String>) {
    var selectedValue by remember { mutableStateOf("this?") }
    val isSelectedItem: (String) -> Boolean = {selectedValue == it}
    val onChangeState: (String) -> Unit = {selectedValue = it}

    Column() {
        listForButton.forEach { item ->
            Column {
                Row(modifier = Modifier
                    .selectable(
                        selected = isSelectedItem(item),
                        onClick = {onChangeState(item)
                            onValueSelect(selectedValue)},
                        role = Role.RadioButton
                    )
                ) {
                    RadioButton(
                        selected = isSelectedItem(item),
                        onClick = {onChangeState(item)
                            onValueSelect(selectedValue)}
                    )
                    Text(text = item)
                }
            }
        }
    }
}

@Preview
@Composable
fun StartScreenPreview(){
    StartScreen(menus = menus, {})
}