package com.example.sleepmanager.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.recreate
import com.example.sleepmanager.App.Companion.prefs
import com.example.sleepmanager.data.DataSource.temporaryRecord
import com.example.sleepmanager.data.MySharedPreferences
import com.example.sleepmanager.ui.SleepRecordScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import com.example.sleepmanager.ui.SleepRecordScreen as SleepRecordScreen1

@Composable
fun SleepRecordScreen(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val calendar = Calendar.getInstance()
    val timeState1 = remember { mutableStateOf("") }
    val timeState2 = remember { mutableStateOf("") }
    val dateState1 = remember { mutableStateOf("") }
    val context = LocalContext.current

    val timePickerDialog1 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
            timeState1.value = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val timePickerDialog2 = TimePickerDialog(
        context,
        { view, hourOfDay, minute ->
        timeState2.value = "${hourOfDay}:${minute}"
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
        true
    )

    val datePickerDialog1 = DatePickerDialog(
        context,
        { view, year, month, dayOfMonth ->
            dateState1.value = "${year}년 ${month +1}월 ${dayOfMonth}일"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    
    var timeStateSleepTimeToAdd by remember{ mutableStateOf("") }
    var timeStateWakeUpTimeToAdd by remember{ mutableStateOf("") }
    var dateStateToAdd by remember{ mutableStateOf("") }
    var feelingStateToAdd by remember{ mutableStateOf("") }

    var showAlertDialog by remember { mutableStateOf(false) }

    if (showAlertDialog) {
        FeelingDialog(
            onDismissRequest = {showAlertDialog = false},
            onOkClick = {
                showAlertDialog = false
                feelingStateToAdd = it},
        )
    }

    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("추가 또는 수정할 수면 기록을 입력하세요")
        Text("날짜 / 취침시각 / 기상시각 / 기상 후 컨디션")
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { datePickerDialog1.show() }) {
                dateStateToAdd = dateState1.value
                Text(text = dateStateToAdd)
            }
            Button(onClick = { timePickerDialog1.show() }) {
                timeStateSleepTimeToAdd = timeState1.value
                Text(text = timeStateSleepTimeToAdd)
            }
            Button(onClick = { timePickerDialog2.show() }) {
                timeStateWakeUpTimeToAdd = timeState2.value
                Text(text = timeStateWakeUpTimeToAdd)
            }
            Button(onClick = { showAlertDialog = true }) {
                Text(text = feelingStateToAdd)
            }
        }
        Button(onClick = {
            onAddButtonClicked(
                dateStateToAdd = dateStateToAdd,
                timeStateSleepTimeToAdd = timeStateSleepTimeToAdd,
                timeStateWakeUpTimeToAdd = timeStateWakeUpTimeToAdd,
                feelingStateToAdd = feelingStateToAdd
            )
            if (prefs.getBoolean("beforeCycleControl",true) == false) {
                prefs.setString("goalDay",(prefs.getString("goalDay","0").toInt()-1).toString())
            }
            sortRecord()
        }) {
            dateStateToAdd = ""
            timeStateSleepTimeToAdd = ""
            timeStateWakeUpTimeToAdd = ""
            Text(text = "추가")
        }
        Spacer(modifier = modifier.height(16.dp))
        // prefs.setInt("numberOfRecord",0)
        LazyColumn() {
            items(prefs.getInt("numberOfRecord",0)) { n ->
                SleepRecordBlock(
                    n = prefs.getInt("numberOfRecord",0)-n-1,
                    dateStateToAdd = dateStateToAdd,
                    timeStateSleepTimeToAdd = timeStateSleepTimeToAdd,
                    timeStateWakeUpTimeToAdd = timeStateWakeUpTimeToAdd,
                    feelingStateToAdd = feelingStateToAdd
                )
                Spacer(modifier = modifier.height(15.dp))
                }
            }
        }
}

@Composable
fun SleepRecordBlock(
    n:Int,
    dateStateToAdd: String,
    timeStateSleepTimeToAdd: String,
    timeStateWakeUpTimeToAdd: String,
    feelingStateToAdd: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                    text = prefs.getString("$n sleepDate", "")
                )
                Text(
                    text = prefs.getString("$n sleepTime", "") + " ~ " +
                            prefs.getString("$n wakeUpTime", "")
                )
                Text(text = prefs.getString("$n feeling", ""))
            }

            Column() {
                Button(
                    onClick = {
                        onEditButtonClicked(
                        n = n,
                        dateStateToAdd = dateStateToAdd,
                        timeStateSleepTimeToAdd = timeStateSleepTimeToAdd,
                        timeStateWakeUpTimeToAdd = timeStateWakeUpTimeToAdd,
                        feelingStateToAdd = feelingStateToAdd
                    )
                        sortRecord()
                              },
                ) {
                    Text(text = "수정")
                }
                Button(
                    onClick = {
                        onDeleteButtonClicked(n=n)
                        if (prefs.getBoolean("beforeCycleControl",true) == false) {
                            prefs.setString("goalDay",(prefs.getString("goalDay","0").toInt()+1).toString())
                        }
                        sortRecord()
                    }
                ) {
                    Text(text = "삭제")
                }
            }
        }
    }
}

fun onAddButtonClicked(
    dateStateToAdd: String,
    timeStateSleepTimeToAdd: String,
    timeStateWakeUpTimeToAdd: String,
    feelingStateToAdd: String
) {
    val n = prefs.getInt("numberOfRecord",0)
    prefs.setInt("numberOfRecord", n+1)
    prefs.setString("$n sleepDate", dateStateToAdd)
    prefs.setString("$n sleepTime", timeStateSleepTimeToAdd)
    prefs.setString("$n wakeUpTime", timeStateWakeUpTimeToAdd)
    prefs.setString("$n feeling", feelingStateToAdd)
}

fun onDeleteButtonClicked(n: Int) {
    val N = prefs.getInt("numberOfRecord",0)
    prefs.setInt("numberOfRecord",N-1)
    for (i in n..N-1) {
        prefs.setString("$i sleepDate", prefs.getString("${i+1} sleepDate",""))
        prefs.setString("$i sleepTime", prefs.getString("${i+1} sleepTime",""))
        prefs.setString("$i wakeUpTime", prefs.getString("${i+1} wakeUpTime",""))
        prefs.setString("$i feeling", prefs.getString("${i+1} feeling", ""))


    }
}

fun onEditButtonClicked(
    n: Int,
    dateStateToAdd: String,
    timeStateSleepTimeToAdd: String,
    timeStateWakeUpTimeToAdd: String,
    feelingStateToAdd: String
) {
    prefs.setString("$n sleepDate", dateStateToAdd)
    prefs.setString("$n sleepTime", timeStateSleepTimeToAdd)
    prefs.setString("$n wakeUpTime", timeStateWakeUpTimeToAdd)
    prefs.setString("$n feeling", feelingStateToAdd)
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

fun sortRecord() {
    val n = prefs.getInt("numberOfRecord",0)
    var M : MutableList<List<String>> = mutableListOf()

    for (i in 0..n-1) {
        M.add(listOf(prefs.getString("$i sleepDate",""),prefs.getString("$i sleepTime",""),
            prefs.getString("$i wakeUpTime",""),prefs.getString("$i feeling","")))
    }
    M.sortWith(compareBy<List<String>>{it[0].split("년 ","월 ","일")[0].toInt()}.thenBy { it[0].split("년 ","월 ","일")[1].toInt() }
        .thenBy { it[0].split("년 ","월 ","일")[2].toInt() }.thenBy { it[1].split(":")[0].toInt() }.thenBy { it[1].split(":")[1].toInt() })
    for (i in 0 until n) {
        prefs.setString("$i sleepDate",M[i][0])
        prefs.setString("$i sleepTime",M[i][1])
        prefs.setString("$i wakeUpTime",M[i][2])
        prefs.setString("$i feeling",M[i][3])
    }
}


/*
@Preview
@Composable
fun SleepRecordScreenPreview() {
    SleepRecordScreen(recordList = temporaryRecord)
}*/