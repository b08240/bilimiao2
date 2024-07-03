package cn.a10miaomiao.bilimiao.compose.comm

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavOptions
import cn.a10miaomiao.bilimiao.compose.R
import org.kodein.di.DI
import org.kodein.di.compose.localDI
import kotlin.reflect.KClass

val defaultNavOptions get() = NavOptions.Builder()
    .setEnterAnim(R.anim.miao_fragment_open_enter)
    .setExitAnim(R.anim.miao_fragment_open_exit)
    .setPopEnterAnim(R.anim.miao_fragment_close_enter)
    .setPopExitAnim(R.anim.miao_fragment_close_exit)
    .build()

@OptIn(ExperimentalStdlibApi::class)
@Composable
inline fun <reified VM : ViewModel> diViewModel(
    di: DI = localDI(),
    key: String = di.hashCode().toHexString(),
): VM {
    return diViewModel(di, key) {
        val constructor = VM::class.java.getDeclaredConstructor(
            DI::class.java
        )
        constructor.newInstance(it)
    }
}

@OptIn(ExperimentalStdlibApi::class)
@Composable
inline fun <reified VM : ViewModel> diViewModel(
    di: DI = localDI(),
    key: String = di.hashCode().toHexString(),
    crossinline initializer: ((di: DI) -> VM),
): VM {
    return androidx.lifecycle.viewmodel.compose.viewModel<VM>(
        key = key,
        initializer = {
            initializer(di)
        }
    )
}