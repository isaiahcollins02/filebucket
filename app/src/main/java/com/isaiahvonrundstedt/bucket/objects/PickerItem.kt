package com.isaiahvonrundstedt.bucket.objects

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PickerItem(@DrawableRes var drawableID: Int, @StringRes var stringID: Int)