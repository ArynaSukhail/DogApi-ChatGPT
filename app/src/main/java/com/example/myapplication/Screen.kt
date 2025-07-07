package com.example.myapplication

sealed class Screen(val route: String) {
    object List : Screen("list")
    object Detail : Screen("detail/{id}") {
        fun createRoute(id: Int) = "detail/$id"
    }
}