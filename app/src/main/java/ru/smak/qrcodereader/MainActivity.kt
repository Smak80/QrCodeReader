package ru.smak.qrcodereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.smak.qrcodereader.database.QrInfo
import ru.smak.qrcodereader.navigation.FabButton
import ru.smak.qrcodereader.navigation.Page
import ru.smak.qrcodereader.ui.theme.QrCodeReaderTheme
import ru.smak.qrcodereader.viewmodels.QrCreateViewModel
import ru.smak.qrcodereader.viewmodels.QrListViewModel

class MainActivity : ComponentActivity() {

    private val lvm by viewModels<QrListViewModel>()
    private val cvm by viewModels<QrCreateViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navCtrl = rememberNavController()
            val navPosition by navCtrl.currentBackStackEntryAsState()
            QrCodeReaderTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            navigationIcon = {
                                if (navPosition?.destination?.route != Page.MAIN.name) {
                                    IconButton(onClick = {
                                        navCtrl.popBackStack()
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                                            contentDescription = stringResource(R.string.back)
                                        )
                                    }
                                }
                            },
                            title = {
                                Text(stringResource(
                                    when(navPosition?.destination?.route){
                                        Page.CREATE.name -> R.string.qr_create_title
                                        else -> R.string.qr_list_title
                                    }
                                ))
                            },
                            actions = {
                                if (navPosition?.destination?.route == Page.CREATE.name && cvm.qrText.isNotBlank()) {
                                    IconButton(onClick = {
                                        lvm.addQrText(cvm.qrText)
                                        navCtrl.popBackStack()
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_save_alt_24),
                                            contentDescription = stringResource(R.string.save)
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            )
                        )
                    },
                    floatingActionButton = {
                        if (navPosition?.destination?.route == Page.MAIN.name)
                        FabsRow(navCtrl) { fabButton ->
                            when(fabButton){
                                FabButton.CREATE -> {
                                    cvm.init()
                                    navCtrl.navigate(Page.CREATE.name)
                                }
                                FabButton.SCAN -> {
                                    lvm.scanQr()
                                }
                            }
                        }
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) { innerPadding ->
                    NavHost(
                        navController = navCtrl, 
                        startDestination = Page.MAIN.name, 
                        Modifier.padding(innerPadding)
                    ) {
                        composable(Page.MAIN.name){
                            MainPage(lvm)
                        }
                        composable(Page.CREATE.name){
                            CreateQrPage(cvm, modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateQrPage(
    viewModel: QrCreateViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        OutlinedTextField(
            value = viewModel.qrText,
            onValueChange = {
                viewModel.updateText(it)
            },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 10,
        )
        BoxWithConstraints {
            val byWidth = maxWidth < maxHeight
            val mod = if (byWidth){
                Modifier.fillMaxWidth()
            } else {
                Modifier.fillMaxHeight()
            }.aspectRatio(1f, !byWidth)
            QrImage(
                viewModel.qrImage,
                modifier = mod
            )
        }
    }
}

@Composable
fun QrImage(
    qrImage: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    qrImage?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = modifier
        )
    } ?: run {
        Icon(
            painter = painterResource(id = R.drawable.twotone_image_not_supported_128),
            contentDescription = null,
            modifier = modifier,
            tint = MaterialTheme.colorScheme.error
        )
    }
}


@Preview
@Composable
fun QrImagePreview(){
    QrImage(qrImage = null)
}

@Composable
fun MainPage(
    viewModel: QrListViewModel,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(viewModel.texts){
            QrCard(
                qrInfo = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                img = viewModel.createQr(it.text)
            )
        }
    }
}

@Composable
fun QrCard(
    qrInfo: QrInfo,
    modifier: Modifier = Modifier,
    img: ImageBitmap? = null,
){
    ElevatedCard(modifier = modifier,) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            BoxWithConstraints {
                val width = this.maxWidth - 64.dp
                Text(
                    qrInfo.text,
                    modifier = Modifier.width(width),
                    fontSize = 14.sp
                )
            }
            QrImage(
                img,
                Modifier
                    .height(64.dp)
                    .aspectRatio(1f, true)
            )
        }
    }
}

@Preview
@Composable
fun QrCardPreview(){
    QrCard(
        QrInfo(text = "Some Text to show lkjfh lkdjh lskjhj dlkjh sdsldkjvbsbdvbsdlkfvhsdldkvhsdlkfjhsdflhsdlfkgkhsdngcposeserignewpeorvhjhjndsdilvhnsdlkjsdn nflv kskjdjhv vlskddkghsd;dlkjknsd;dfjnsd;lgkks sd;dflkv "),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun FabsRow(
    navCtrl: NavHostController,
    modifier: Modifier = Modifier,
    onClick: (FabButton) -> Unit = {}
){
    val navPosition by navCtrl.currentBackStackEntryAsState()
    Row(
        modifier = modifier
            .wrapContentSize()
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Fab(
            iconId = R.drawable.baseline_qr_code_2_32,
            textId = R.string.create_qr_btn,
            modifier = Modifier.weight(1f),
        ){
            onClick(FabButton.CREATE)
        }
        Fab(
            iconId = R.drawable.baseline_qr_code_scanner_32,
            textId = R.string.scan_qr_btn,
            modifier = Modifier.weight(1f),
        ){
            onClick(FabButton.SCAN)
        }
    }
}

@Preview
@Composable
fun FabsRowPreview(){
    val navCtrl = rememberNavController()
    FabsRow(navCtrl)
}

@Composable
fun Fab(
    @DrawableRes iconId: Int,
    @StringRes textId: Int,
    modifier: Modifier = Modifier,
    onClick: ()->Unit = {},
){
    FloatingActionButton(onClick = onClick, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null
            )
            Text(
                stringResource(textId),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Preview
@Composable
fun FabPreview(){
    Fab(R.drawable.baseline_qr_code_2_32, R.string.create_qr_btn)
}