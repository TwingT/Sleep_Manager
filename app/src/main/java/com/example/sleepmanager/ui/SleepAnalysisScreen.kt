package com.example.sleepmanager.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleepmanager.App.Companion.prefs

@Composable
fun SleepAnalysis(
    sleepStageInformation: List<Double>,  // 3개의 Double은 initialTime, lengthNonRem, lengthRem
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    if (sleepStageInformation == listOf(0.0,0.0,0.0)) {
        Text(text = "No data!!!", fontSize = 100.sp)
    } else {

        val sleepStageMutableListForGraph =
            sleepStageMutableListForGraph(sleepStageInformation)  // MutableList< nonRem or Rem, length >

        Column(
            modifier = modifier.padding(16.dp),
        ) {
            Spacer(modifier = modifier.height(70.dp))
            Row(
                modifier = modifier.height(200.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = "awake", fontSize = 13.sp)
                    Text(text = "REM", fontSize = 13.sp)
                    Text(text = "non-REM", fontSize = 13.sp)
                    Text(text = "")
                    Text(text = "")
                }
                Column() {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(start = 10.dp)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        drawPath(
                            path = Path().apply {
                                moveTo(0F, 0F)
                                lineTo(
                                    canvasWidth * (sleepStageInformation[0] * 1 / 9).toFloat(),
                                    canvasHeight * 2 / 3F
                                )
                                lineTo(
                                    canvasWidth * (sleepStageInformation[0] * 1 / 9).toFloat(),
                                    canvasHeight
                                )
                                lineTo(0F, canvasHeight)
                                close()
                            },
                            color = Color.Black
                        )

                        sleepStageMutableListForGraph.forEach {
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(
                                    canvasWidth * (it[1] * 1 / 9).toFloat(),
                                    canvasHeight * (1 - it[0] * 1 / 3).toFloat()
                                ),
                                size = Size(
                                    canvasWidth * (it[2] * 1 / 9).toFloat(),
                                    canvasHeight * (it[0] * 1 / 3).toFloat()
                                )
                            )
                        }
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "0")
                        Text(text = "1")
                        Text(text = "2")
                        Text(text = "3")
                        Text(text = "4")
                        Text(text = "5")
                        Text(text = "6")
                        Text(text = "7")
                        Text(text = "8")
                        Text(text = "9")
                    }
                }
            }
            Spacer(modifier = modifier.height(50.dp))
            Text(text = "권장 수면시간 :", fontSize = 20.sp)
            Text(
                text = recommendedSleepTime(sleepStageMutableListForGraph = sleepStageMutableListForGraph),
                fontSize = 20.sp
            )
        }
    }
}

private fun sleepStageMutableListForGraph(
    sleepStageInformation: List<Double>
): MutableList<List<Double>> {   // listOf(stageName, start, length)
    val sleepStageMutableListForGraph: MutableList<List<Double>> = mutableListOf()

    var stageName = 1.0  // nonRem 1.0, Rem 2.0
    var sum: Double = sleepStageInformation[0]
    var length: Double = sleepStageInformation[1]

    while (sum < 9.0) {
        if (sum+length > 9.0) {
            sleepStageMutableListForGraph.add(listOf(stageName,sum,9.0-sum))
        } else {
            sleepStageMutableListForGraph.add(listOf(stageName,sum,length))
        }
        sum = sum + length
        if (stageName == 1.0) {
            stageName = 2.0
        } else { stageName = 1.0 }
        length = sleepStageInformation[stageName.toInt()]
    }
    

    
    return sleepStageMutableListForGraph
}

private fun recommendedSleepTime(
    sleepStageMutableListForGraph: MutableList<List<Double>>
):String {
    var output: String = ""

    var count = 0
    sleepStageMutableListForGraph.forEach {
        if (it[0] == 2.0) {
            if (it[1] >= 4.0) {
                output = output + (round(it[1] * 10) / 10).toString() + "시간 ~ " + (round((it[1] + it[2]) * 10) / 10).toString() + "시간, "
                prefs.setString("$count remStart",it[1].toString())
                prefs.setString("$count remEnd", (it[1]+it[2]).toString())
                count += 1
            } else if (it[1]+it[2] > 4.0) {
                output = output + "4시간 ~ " + (round((it[1] + it[2]) * 10) / 10).toString() + "시간, "
                prefs.setString("$count remStart","4")
                prefs.setString("$count remEnd", (it[1]+it[2]).toString())
                count += 1
            }
        }
    }
    prefs.setInt("numberOfRem",count)
    output = output.slice(0..output.length-3)

    return output
}

@Preview
@Composable
fun SleepAnalysisScreenPreview() {
    SleepAnalysis(listOf(0.4,0.9,0.6))
}