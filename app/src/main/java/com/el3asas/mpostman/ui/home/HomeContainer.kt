package com.el3asas.mpostman.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.el3asas.mpostman.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMainContainer(
    modifier: Modifier,
    viewModel: MainViewModel?
) {
    val responseValue by remember {
        viewModel?.httpRequestResponse ?: mutableStateOf("")
    }
    Scaffold(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            UrlEnteringPart(modifier = Modifier, viewModel = viewModel, mUrl = viewModel?.mUrl)

            Spacer(modifier = Modifier.padding(16.dp))

            PrintResults(modifier = Modifier, mResponse = responseValue)

            Spacer(modifier = Modifier.padding(20.dp))

            SubmitRequestBtn(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlEnteringPart(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel?,
    mUrl: MutableState<String>?
) {
    var url by remember { mUrl ?: mutableStateOf("") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        OutlinedTextField(modifier = Modifier.fillMaxWidth(.7f), value = url, onValueChange = {
            url = it
        })
        Spacer(modifier = Modifier.padding(5.dp))
        RequestTypesDropDown(
            modifier = Modifier.fillMaxWidth(),
            mSelectedItem = viewModel?.selectedRequestType
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestTypesDropDown(modifier: Modifier = Modifier, mSelectedItem: MutableState<String>?) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    var selectedItem by remember {
        mSelectedItem ?: mutableStateOf("POST")
    }
    val menuItems = listOf("POST", "GET")
    val icon = if (isExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxSize(),
            value = selectedItem,
            onValueChange = { selectedItem = it },
            trailingIcon = {
                Icon(icon, "contentDescription", Modifier.clickable { isExpanded = !isExpanded })
            }
        )

        DropdownMenu(
            modifier = modifier
                .background(Color.White, RoundedCornerShape(5)),
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }) {
            menuItems.forEach { itemValue: String ->
                DropdownMenuItem(
                    text = { Text(text = itemValue) },
                    onClick = {
                        isExpanded = isExpanded.not()
                        selectedItem = itemValue
                    })
            }
        }
    }
}

@Composable
fun PrintResults(modifier: Modifier = Modifier, mResponse: String) {
    Box(modifier = modifier.padding(8.dp)) {
        Text(text = mResponse)
    }
}

@Composable
fun SubmitRequestBtn(viewModel: MainViewModel?) {
    Button(onClick = {
        viewModel?.sendRequest()
    }) {
        Text(text = "Send Request")
    }
}