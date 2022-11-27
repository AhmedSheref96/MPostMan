package com.el3asas.mpostman.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.el3asas.mpostman.MainViewModel
import com.el3asas.mpostman.ui.home.ui_states.ParamModelUiState
import com.google.accompanist.pager.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


const val RAW_BODY_TYPE = 0
const val FORM_DATA_BODY_TYPE = 1


@Preview(showBackground = true)
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
/*
    LaunchedEffect(Unit) {
        snapshotFlow { responseValue }
            .collect {
                if (it.isEmpty().not())
                    mainPagesTransaction = 1
            }
    }
*/
    Scaffold(floatingActionButton = {
        SubmitRequestBtn(viewModel = viewModel) {
            mainPagesTransaction = 1
        }
    }) {
        ConstraintLayout(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            val (baseUrlRef, pathUrlRef, requestTypeRef, pagerRef) = createRefs()
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
        val itemsTitles = listOf("params", "body")
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            for (itemPos in 0 until 2) {
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
            count = 2
        ) { page ->
            when (page) {
                0 -> ParamsView(modifier = modifier, viewModel = viewModel)
                1 -> BodyEntersView(modifier = modifier, viewModel = viewModel)
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
        Row(
            modifier = modifier
                .background(Color(0xFFCCCCCC))
                .fillMaxWidth()
        ) {
            TabBtn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(.5f),
                btnString = "Inputs",
                textColor =
                if (pageTransformation == 0) MaterialTheme.colors.primary
                else Color(0xFF2C2C2C)
            ) {
                coroutineScope.launch {
                    onClickTab(0)
                }
            }
            TabBtn(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                btnString = "Results",
                textColor =
                if (pageTransformation == 1) MaterialTheme.colors.primary
                else Color(0xFF2C2C2C)
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
                ResultsPage(mResponse = mResponse, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BodyEntersView(modifier: Modifier = Modifier, viewModel: MainViewModel?) {
    var bodyType by remember {
        viewModel!!.bodySelectedType
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
        ) {
            IconButton(modifier = modifier
                .height(45.dp)
                .fillMaxWidth(.5f),
                onClick = {
                    if (bodyType != RAW_BODY_TYPE)
                        bodyType = RAW_BODY_TYPE
                }) {
                Row(
                    Modifier.fillMaxHeight(),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = bodyType == RAW_BODY_TYPE,
                        onClick = {
                            if (bodyType != RAW_BODY_TYPE)
                                bodyType = RAW_BODY_TYPE
                        })
                    Text(text = "raw-data")
                }
            }
            IconButton(modifier = modifier
                .height(45.dp)
                .fillMaxWidth(),
                onClick = {
                    if (bodyType != FORM_DATA_BODY_TYPE)
                        bodyType = FORM_DATA_BODY_TYPE
                }) {
                Row(
                    Modifier.fillMaxHeight(),
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(
                        selected = bodyType == FORM_DATA_BODY_TYPE,
                        onClick = {
                            if (bodyType != FORM_DATA_BODY_TYPE)
                                bodyType = FORM_DATA_BODY_TYPE
                        })
                    Text(text = "form-data")
                }
            }
        }

        if (bodyType == RAW_BODY_TYPE) {
            RawDataBodyView(viewModel = viewModel)
        } else {
            FormDataBodyView(viewModel = viewModel)
        }
    }
}

@Composable
fun RawDataBodyView(modifier: Modifier = Modifier, viewModel: MainViewModel?) {
    var rawDataBodyValues by remember {
        viewModel!!.rawBodyValues
    }
    val scrollState = rememberScrollState()

    TextField(
        value = rawDataBodyValues,
        modifier = modifier
            .fillMaxSize()
            .scrollable(
                state = scrollState,
                orientation = Orientation.Vertical,
                enabled = true
            ),
        onValueChange = {
            rawDataBodyValues = it
        },
        placeholder = {
            Text(text = "Enter json body")
        }
    )
}

@Composable
fun FormDataBodyView(modifier: Modifier = Modifier, viewModel: MainViewModel?) {
    val formDataBodyValues = remember {
        viewModel!!.formDataBodyValues
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = rememberForeverLazyListState(key = "Overview")
    ) {
        itemsIndexed(items = formDataBodyValues) { index: Int, item: MutableState<ParamModelUiState> ->
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
            val rowModifier = Modifier

            Row(modifier = rowModifier.fillMaxWidth()) {
                val paramValue by item
                TextField(
                    modifier = rowModifier
                        .fillMaxWidth(.45f)
                        .padding(8.dp),
                    value = paramValue.name.value,
                    onValueChange = {
                        paramValue.name.value = it
                    })
                TextField(
                    modifier = rowModifier
                        .fillMaxWidth(.8f)
                        .padding(8.dp),
                    value = paramValue.value.value,
                    onValueChange = {
                        paramValue.value.value = it
                    })
                IconButton(
                    modifier = rowModifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(CenterVertically),
                    onClick = {
                        if (formDataBodyValues.size <= 1)
                            formDataBodyValues[index].value = ParamModelUiState()
                        else
                            formDataBodyValues.remove(item)
                    }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.Delete),
                        tint = Color.Red,
                        contentDescription = ""
                    )
                }
            }
            if (index == formDataBodyValues.size - 1) {
                Column {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = { formDataBodyValues.add(mutableStateOf(ParamModelUiState())) }
                    ) {
                        Text(text = "Add")
                        Icon(
                            imageVector = Icons.Filled.Add,
                            tint = Color(0xF0FFFFFF),
                            contentDescription = ""
                        )
                    }
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun ParamsView(modifier: Modifier = Modifier, viewModel: MainViewModel?) {
    val paramsValues = remember {
        viewModel!!.paramsValues
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = rememberForeverLazyListState(key = "Overview")
    ) {
        itemsIndexed(items = paramsValues) { index: Int, item: MutableState<ParamModelUiState> ->
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
            val rowModifier = Modifier

            Row(modifier = rowModifier.fillMaxWidth()) {
                val paramValue by item
                TextField(
                    modifier = rowModifier
                        .fillMaxWidth(.45f)
                        .padding(8.dp),
                    value = paramValue.name.value,
                    onValueChange = {
                        paramValue.name.value = it
                    })
                TextField(
                    modifier = rowModifier
                        .fillMaxWidth(.8f)
                        .padding(8.dp),
                    value = paramValue.value.value,
                    onValueChange = {
                        paramValue.value.value = it
                    })
                IconButton(
                    modifier = rowModifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(CenterVertically),
                    onClick = {
                        if (paramsValues.size <= 1)
                            paramsValues[index].value = ParamModelUiState()
                        else
                            paramsValues.remove(item)
                    }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Filled.Delete),
                        tint = Color.Red,
                        contentDescription = ""
                    )
                }
            }
            if (index == paramsValues.size - 1) {
                Column {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            paramsValues.add(mutableStateOf(ParamModelUiState()))
                        }
                    ) {
                        Text(text = "Add Param")
                        Icon(
                            imageVector = Icons.Filled.Add,
                            tint = Color(0xF0FFFFFF),
                            contentDescription = ""
                        )
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }

            }
        }
    }
}

@Composable
fun ResultsPage(
    modifier: Modifier = Modifier,
    mResponse: String,
    viewModel: MainViewModel?
) {
    val isLoading by remember { viewModel!!.isLoading }
    if (isLoading) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.LightGray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = CenterHorizontally
        ) {
            CircularProgressIndicator(color = Color.Blue)
        }
    } else {
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(enabled = true, state = scrollState)
                .background(Color.LightGray)
        ) {
            var result = mResponse
            try {
                result = JSONArray(mResponse).toString(2)
            } catch (ignored: Exception) {
                try {
                    result = JSONObject(mResponse).toString(2)
                } catch (ignored: Exception) {
                }
            }

            Text(modifier = modifier, text = result)
        }
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
            readOnly = true,
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
fun TabBtn(modifier: Modifier = Modifier, textColor: Color, btnString: String, action: () -> Unit) {
    TextButton(
        modifier = modifier,
        onClick = {
            action()
        }) {
        Text(text = btnString, color = textColor)
    }
}

@Composable
fun SubmitRequestBtn(viewModel: MainViewModel?, onClickSendRequest: () -> Unit) {
    val isLoading by remember { viewModel!!.isLoading }
    if (isLoading.not()) {
        FloatingActionButton(
            onClick = {
                viewModel?.sendRequest()
                onClickSendRequest()
            }) {
            Icon(
                imageVector = Icons.Filled.Send,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(320f),
                contentDescription = "send request"
            )
        }
    } else {
        FloatingActionButton(onClick = {}) {
            CircularProgressIndicator(color = Color.Blue)
        }
    }
}

@Composable
fun rememberForeverLazyListState(
    key: String,
    params: String = "",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        var savedValue = SaveMap[key]
        if (savedValue?.params != params) savedValue = null
        val savedIndex = savedValue?.index ?: initialFirstVisibleItemIndex
        val savedOffset = savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        LazyListState(
            savedIndex,
            savedOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            val lastIndex = scrollState.firstVisibleItemIndex
            val lastOffset = scrollState.firstVisibleItemScrollOffset
            SaveMap[key] = KeyParams(params, lastIndex, lastOffset)
        }
    }
    return scrollState
}

private val SaveMap = mutableMapOf<String, KeyParams>()

private data class KeyParams(
    val params: String = "",
    val index: Int,
    val scrollOffset: Int
)