package com.el3asas.mpostman.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.el3asas.models.ParamModel
import com.el3asas.mpostman.MainViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeMainContainer(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel? = null
) {
    val responseValue by remember {
        viewModel?.httpRequestResponse ?: mutableStateOf("")
    }

    val coroutineScope = rememberCoroutineScope()
    var mainPagesTransaction by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { responseValue }
            .collect {
                if (it.isEmpty().not())
                    mainPagesTransaction = 1
            }
    }

    Scaffold(floatingActionButton = {
        SubmitRequestBtn(viewModel = viewModel)
    }) {
        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            val (baseUrlRef, pathUrlRef, requestTypeRef, pagerRef, inputsTab, resultsTab, submitRequestRef) = createRefs()
            BaseUrlPart(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .constrainAs(baseUrlRef) {
                        top.linkTo(parent.top)
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        absoluteRight.linkTo(parent.absoluteRight)
                    }, mBaseUrl = viewModel?.baseUrl
            )

            UrlEnteringPart(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(pathUrlRef) {
                        top.linkTo(baseUrlRef.bottom)
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        absoluteRight.linkTo(requestTypeRef.absoluteLeft)
                    },
                viewModel = viewModel,
                mPathUrl = viewModel?.pathUrl
            )

            MainPages(
                modifier = Modifier
                    .constrainAs(pagerRef) {
                        top.linkTo(pathUrlRef.bottom)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
                pageTransformation = mainPagesTransaction,
                coroutineScope = coroutineScope,
                viewModel = viewModel,
                mResponse = responseValue
            ) { pageIndex ->
                mainPagesTransaction = pageIndex
            }
//            TabBtn(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .constrainAs(resultsTab) {
//                        bottom.linkTo(parent.bottom)
//                        absoluteLeft.linkTo(inputsTab.absoluteRight)
//                        absoluteRight.linkTo(parent.absoluteRight)
//                        width = Dimension.fillToConstraints
//                    }, btnString = "Results"
//            ) {
//                coroutineScope.launch {
//                    mainPagesTransaction = 1
//                }
//            }
//
//            TabBtn(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .constrainAs(inputsTab) {
//                        bottom.linkTo(parent.bottom)
//                        absoluteRight.linkTo(resultsTab.absoluteLeft)
//                        absoluteLeft.linkTo(parent.absoluteLeft)
//                        width = Dimension.fillToConstraints
//                    }, btnString = "Inputs"
//            ) {
//                coroutineScope.launch {
//                    mainPagesTransaction = 0
//                }
//            }

        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun InputsPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    viewModel: MainViewModel?
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        val itemsTitles = listOf("headers", "body", "headers", "body", "headers", "body")
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            for (itemPos in 0 until 6) {
                Tab(modifier = Modifier.background(MaterialTheme.colors.primarySurface),
                    selected = pagerState.currentPage == itemPos,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(itemPos)
                        }
                    }
                ) {
                    Text(
                        text = itemsTitles[itemPos],
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        HorizontalPager(
            modifier = modifier
                .fillMaxSize(),
            state = pagerState,
            count = 6
        ) { page ->
            when (page) {
                0 -> {
                    ParamsView(modifier = modifier, viewModel = viewModel)
                }
                1 -> {
                    BodyEntersView(modifier = modifier, viewModel = viewModel)
                }
                2 -> {
                    ParamsView(modifier = modifier, viewModel = viewModel)
                }
                3 -> {
                    BodyEntersView(modifier = modifier, viewModel = viewModel)
                }
                4 -> {
                    ParamsView(modifier = modifier, viewModel = viewModel)
                }
                5 -> {
                    BodyEntersView(modifier = modifier, viewModel = viewModel)
                }
            }
        }

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainPages(
    modifier: Modifier = Modifier,
    pageTransformation: Int,
    coroutineScope: CoroutineScope,
    viewModel: MainViewModel?,
    mResponse: String,
    onClickTab: (Int) -> Unit
) {
    Column(modifier = modifier) {
        Row(modifier = modifier.fillMaxWidth()) {
            TabBtn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(.5f),
                btnString = "Inputs"
            ) {
                coroutineScope.launch {
                    onClickTab(0)
                }
            }
            TabBtn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                btnString = "Results"
            ) {
                coroutineScope.launch {
                    onClickTab(1)
                }
            }
        }

        when (pageTransformation) {
            0 -> {
                val inputPagerState = rememberPagerState()
                InputsPager(
                    modifier = modifier,
                    pagerState = inputPagerState,
                    coroutineScope = coroutineScope,
                    viewModel = viewModel
                )
            }
            1 -> {
                ResultsPage(mResponse = mResponse)
            }
        }
    }
}

/*
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    viewModel: MainViewModel?,
    mResponse: String
) {
    val inputPagerState = rememberPagerState()
    Box(modifier = modifier.disabledHorizontalPointerInputScroll()) {
        HorizontalPager(
            count = 2,
            modifier = Modifier,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> {
                    InputsPager(
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        pagerState = inputPagerState
                    )
                }
                1 -> {
                    ResultsPage(mResponse = mResponse)
                }
            }
        }
    }

}
*/
@Composable
fun BodyEntersView(modifier: Modifier = Modifier, viewModel: MainViewModel?) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Text(text = "enter body enters here", modifier = Modifier.align(CenterHorizontally))
    }
}

@Composable
fun ParamsView(modifier: Modifier = Modifier, viewModel: MainViewModel?) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        itemsIndexed(items = viewModel?.paramsValues!!.toList()) { index: Int, item: MutableState<ParamModel> ->
            if (index == 0) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                            .background(Color.LightGray)
                            .padding(8.dp),
                        text = "KEY",
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),
                        text = "VALUE"
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                val paramItem by remember { item }
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .padding(8.dp),
                    value = paramItem.name,
                    onValueChange = {
                        paramItem.name = it
                    })
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    value = paramItem.value,
                    onValueChange = {
                        paramItem.value = it
                    })
            }
            if (index == viewModel.paramsValues.size - 1) {
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = {
                        viewModel.paramsValues.add(
                            mutableStateOf(
                                ParamModel(
                                    name = "",
                                    value = ""
                                )
                            )
                        )
                    }
                ) {
                    Text(text = "Add Param")
                    Icon(
                        imageVector = Icons.Filled.Add,
                        tint = Color(0xF0FFFFFF),
                        contentDescription = ""
                    )
                }
            }
        }
        
    }
}

@Composable
fun ResultsPage(
    modifier: Modifier = Modifier,
    mResponse: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Green)
    ) {
        Text(modifier = modifier, text = mResponse)
    }
}

@Composable
fun BaseUrlPart(
    modifier: Modifier = Modifier,
    mBaseUrl: MutableState<String>?
) {
    var baseUrl by remember { mBaseUrl ?: mutableStateOf("") }
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = baseUrl,
        onValueChange = {
            baseUrl = it
        })
}

@Composable
fun UrlEnteringPart(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel?,
    mPathUrl: MutableState<String>?
) {
    var pathUrl by remember { mPathUrl ?: mutableStateOf("") }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        OutlinedTextField(modifier = Modifier.fillMaxWidth(.6f), value = pathUrl, onValueChange = {
            pathUrl = it
        })
        Spacer(modifier = Modifier.padding(5.dp))
        RequestTypesDropDown(
            modifier = Modifier.fillMaxWidth(),
            mSelectedItem = viewModel?.selectedRequestType
        )
    }
}

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
                    content = { Text(text = itemValue) },
                    onClick = {
                        isExpanded = isExpanded.not()
                        selectedItem = itemValue
                    })
            }
        }
    }
}

@Composable
fun TabBtn(modifier: Modifier = Modifier, btnString: String, action: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = {
            action()
        }) {
        Text(text = btnString)
    }
}

@Composable
fun SubmitRequestBtn(viewModel: MainViewModel?) {
    ExtendedFloatingActionButton(
        text = {
            Row {
                Text(text = "send request")
                Icon(
                    imageVector = Icons.Filled.Send,
                    modifier = Modifier.size(24.dp),
                    contentDescription = "send request"
                )
            }
        },
        onClick = {
            viewModel?.sendRequest()
        })
}