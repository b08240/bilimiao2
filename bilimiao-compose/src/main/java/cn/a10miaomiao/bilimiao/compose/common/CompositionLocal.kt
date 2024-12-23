package cn.a10miaomiao.bilimiao.compose.common

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController


private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

internal val LocalContainerView = staticCompositionLocalOf<ViewGroup?> {
    noLocalProvidedFor("LocalFragment")
}

@Composable
fun localContainerView() = LocalContainerView.current

internal val LocalPageRouter = staticCompositionLocalOf<PageRouter> {
    noLocalProvidedFor("LocalPageRouter")
}

@Composable
fun localPageRouter() = LocalPageRouter.current


