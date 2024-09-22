package cn.a10miaomiao.bilimiao.compose.pages.user


import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import cn.a10miaomiao.bilimiao.compose.base.ComposePage
import cn.a10miaomiao.bilimiao.compose.base.stringPageArg
import cn.a10miaomiao.bilimiao.compose.comm.diViewModel
import cn.a10miaomiao.bilimiao.compose.comm.foundation.pagerTabIndicatorOffset
import cn.a10miaomiao.bilimiao.compose.comm.localContainerView
import cn.a10miaomiao.bilimiao.compose.comm.mypage.PageConfig
import cn.a10miaomiao.bilimiao.compose.comm.toPaddingValues
import cn.a10miaomiao.bilimiao.compose.commponents.layout.chain_scrollable.ChainScrollableLayout
import cn.a10miaomiao.bilimiao.compose.commponents.layout.chain_scrollable.rememberChainScrollableLayoutState
import cn.a10miaomiao.bilimiao.compose.pages.message.content.AtMessageContent
import cn.a10miaomiao.bilimiao.compose.pages.message.content.LikeMessageContent
import cn.a10miaomiao.bilimiao.compose.pages.message.content.ReplyMessageContent
import cn.a10miaomiao.bilimiao.compose.pages.user.commponents.UserSpaceHeader
import cn.a10miaomiao.bilimiao.compose.pages.user.content.UserArchiveListContent
import cn.a10miaomiao.bilimiao.compose.pages.user.content.UserDynamicListContent
import cn.a10miaomiao.bilimiao.compose.pages.user.content.UserSpaceIndexContent
import com.a10miaomiao.bilimiao.store.WindowStore
import kotlinx.coroutines.launch
import org.kodein.di.bindSingleton
import org.kodein.di.compose.rememberInstance
import org.kodein.di.compose.subDI
import kotlin.math.roundToInt

class UserSpacePage : ComposePage() {

    val id = stringPageArg("id")

    override val route: String
        get() = "user/space/${id}"

    @Composable
    override fun AnimatedContentScope.Content(navEntry: NavBackStackEntry) {
        val vmid = navEntry.arguments?.get(id) ?: ""
        val viewModel = diViewModel() {
            UserSpaceViewModel(it, vmid)
        }
        UserSpacePageContent(viewModel)
    }
}

@Composable
private fun UserSpacePageContent(
    viewModel: UserSpaceViewModel
) {
    PageConfig(
        title = "用户详情"
    )
    val windowStore: WindowStore by rememberInstance()
    val windowState = windowStore.stateFlow.collectAsState().value
    val windowInsets = windowState.getContentInsets(localContainerView())

    val detailData = viewModel.detailData.collectAsState().value

    if (detailData == null) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column {
                Text("加载中")
            }
        }
        return
    }

    val maxHeaderSize = remember { mutableStateOf(0 to 0) }
    val density = LocalDensity.current
    val chainScrollableLayoutState = rememberChainScrollableLayoutState(
        density.run { maxHeaderSize.value.second.toDp() },
        windowInsets.topDp.dp,
    )
    val isLargeScreen = remember(maxHeaderSize.value.first) {
        density.run { maxHeaderSize.value.first.toDp() } > 600.dp
    }
    val scrollableState = rememberScrollState()

    val scope = rememberCoroutineScope()

    ChainScrollableLayout(
        modifier = Modifier.fillMaxSize(),
        state = chainScrollableLayoutState,
    ) { state ->
        val alpha = (state.maxPx + state.getOffsetYValue()) / state.maxPx
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        0,
                        state
                            .getOffsetYValue()
                            .roundToInt()
                    )
                }
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(state.nestedScroll)
                .scrollable(scrollableState, Orientation.Vertical),
        ) {
            UserSpaceHeader(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                    .alpha(alpha)
                    .onGloballyPositioned { coordinates ->
                        val headerHeight = coordinates.size.height
                        val headerWidth = coordinates.size.width
                        if (maxHeaderSize.value.first != headerWidth ||
                            maxHeaderSize.value.second != headerHeight
                        ) {
                            maxHeaderSize.value = headerWidth to headerHeight
                        }
                    },
                isLargeScreen = isLargeScreen,
                viewModel = viewModel,
            )
        }
        Column(
            modifier = Modifier
                .offset {
                    IntOffset(
                        0,
                        (state.maxPx + state.getOffsetYValue()).roundToInt()
                    )
                },
        ) {
            TabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        start = windowInsets.leftDp.dp,
                        end = windowInsets.rightDp.dp,
                    )
                    .nestedScroll(state.nestedScroll)
                    .scrollable(scrollableState, Orientation.Vertical),
                selectedTabIndex = viewModel.pagerState.currentPage,
                indicator = { positions ->
                    TabRowDefaults.PrimaryIndicator(
                        Modifier.pagerTabIndicatorOffset(viewModel.pagerState, positions),
                    )
                },
            ) {
                viewModel.tabs.forEachIndexed { index, tab ->
                    Tab(
                        text = {
                            Text(
                                text = tab.name,
                                color = if (index == viewModel.currentPage) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground
                                }
                            )
                        },
                        selected = viewModel.currentPage == index,
                        onClick = {
                            scope.launch {
                               viewModel.changeTab(index, true)
                            }
                        },
                    )
                }
            }
            val saveableStateHolder = rememberSaveableStateHolder()
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(bottom = state.minScrollPosition),
                state = viewModel.pagerState,
            ) { index ->
                saveableStateHolder.SaveableStateProvider(index) {
                    viewModel.tabs[index].PageContent()
                }
            }
        }
    }
}