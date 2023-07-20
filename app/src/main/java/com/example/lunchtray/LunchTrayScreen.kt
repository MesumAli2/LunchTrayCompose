/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen
import kotlinx.coroutines.flow.collect

// TODO: Screen enum

enum class LaunchTrayScreen(@StringRes val title : Int){
    START(title = R.string.start_order ),
    EntreeMenu(title = R.string.choose_entree),
    SideDishMenu(title = R.string.choose_side_dish),
    AccompanimentMenu(title = R.string.choose_accompaniment),
    Checkout(title = R.string.order_checkout)
}
// TODO: AppBar

@Composable
fun LunchTrayApp() {
    // TODO: Create Controller and initialization
    val navController : NavHostController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = LaunchTrayScreen.valueOf(
        backStackEntry?.destination?.route ?: LaunchTrayScreen.START.name
    )
    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            // TODO: AppBar
            TopAppBar(title = { Text(text = stringResource(currentScreen.title))},
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier,
                navigationIcon = {
                    if (navController.currentBackStackEntry != null){
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(imageVector =Icons.Filled.ArrowBack , contentDescription = stringResource(
                                id = R.string.back_button
                            ))
                        }
                    }
                }
                )
        }
    ) { innerPadding ->

        // TODO: Navigation host
        val uiState = viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = LaunchTrayScreen.START.name,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(route = LaunchTrayScreen.START.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navController.navigate(LaunchTrayScreen.EntreeMenu.name)
                    },
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .fillMaxSize()
                )
            }

            composable(route = LaunchTrayScreen.EntreeMenu.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LaunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LaunchTrayScreen.SideDishMenu.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateEntree(item)
                    },
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .verticalScroll(rememberScrollState())
                )
            }

            composable(route = LaunchTrayScreen.SideDishMenu.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LaunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LaunchTrayScreen.AccompanimentMenu.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateSideDish(item)
                    },
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .verticalScroll(rememberScrollState())
                )
            }

            composable(route = LaunchTrayScreen.AccompanimentMenu.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LaunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LaunchTrayScreen.Checkout.name)
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateAccompaniment(item)
                    },
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .verticalScroll(rememberScrollState())
                )
            }

            composable(route = LaunchTrayScreen.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LaunchTrayScreen.START.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LaunchTrayScreen.START.name, inclusive = false)
                    },
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    
    }
}
