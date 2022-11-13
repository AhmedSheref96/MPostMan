package com.el3asas.mpostman.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.el3asas.mpostman.MainViewModel
import com.el3asas.mpostman.utils.disabledHorizontalPointerInputScroll
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
    var mainPagerState = rememberPagerState()
    var mainPagesTransaction by remember {
        mutableStateOf(0)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { responseValue }
            .collect {
                if (it.isEmpty().not())
                    mainPagesTransaction = 1
//                    mainPagerState = PagerState(1)
            }
    }

    Scaffold {
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

//            MainPager(
//                modifier = Modifier
//                    .constrainAs(pagerRef) {
//                        top.linkTo(pathUrlRef.bottom)
//                        bottom.linkTo(parent.bottom)
//                        height = Dimension.fillToConstraints
//                    },
//                pagerState = mainPagerState,
//                coroutineScope = coroutineScope,
//                viewModel = viewModel,
//                mResponse = responseValue
//            )

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
            )
            TabBtn(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(resultsTab) {
                        bottom.linkTo(submitRequestRef.top)
                        absoluteLeft.linkTo(inputsTab.absoluteRight)
                        absoluteRight.linkTo(parent.absoluteRight)
                        width = Dimension.fillToConstraints
                    }, btnString = "Results"
            ) {
                coroutineScope.launch {
//                    mainPagerState.animateScrollToPage(1)
                    mainPagesTransaction = 1
                }
            }

            TabBtn(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(inputsTab) {
                        bottom.linkTo(submitRequestRef.top)
                        absoluteRight.linkTo(resultsTab.absoluteLeft)
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        width = Dimension.fillToConstraints
                    }, btnString = "Inputs"
            ) {
                coroutineScope.launch {
//                    mainPagerState.animateScrollToPage(0)
                    mainPagesTransaction = 0
                }
            }

            SubmitRequestBtn(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .constrainAs(submitRequestRef) {
                        bottom.linkTo(parent.bottom)
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        absoluteRight.linkTo(parent.absoluteRight)
                    }, viewModel
            )
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
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainPages(
    modifier: Modifier = Modifier,
    pageTransformation: Int,
    coroutineScope: CoroutineScope,
    viewModel: MainViewModel?,
    mResponse: String
) {
    Box(modifier = modifier) {
        when (pageTransformation) {
            0 -> {
                val inputPagerState = rememberPagerState()
                InputsPager(
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Red)
    ) {
        Text(text = "enter params here", modifier = Modifier.align(CenterHorizontally))
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
fun PrintResults(modifier: Modifier = Modifier, mResponse: String) {
    Box(modifier = modifier.padding(8.dp)) {
        Text(text = mResponse)
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
fun SubmitRequestBtn(modifier: Modifier = Modifier, viewModel: MainViewModel?) {
    Button(modifier = modifier, onClick = {
        viewModel?.sendRequest()
    }) {
        Text(text = "Send Request")
    }
}