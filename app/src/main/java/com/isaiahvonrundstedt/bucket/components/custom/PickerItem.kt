package com.isaiahvonrundstedt.bucket.components.custom

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PickerItem(@DrawableRes var drawableID: Int, @StringRes var stringID: Int)