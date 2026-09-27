package dev.alnoer.drivenext.extensions

import androidx.fragment.app.Fragment
import dev.alnoer.drivenext.DriveNextApplication
import dev.alnoer.drivenext.di.AppContainer

val Fragment.appContainer: AppContainer
    get() = (requireActivity().application as DriveNextApplication).container
