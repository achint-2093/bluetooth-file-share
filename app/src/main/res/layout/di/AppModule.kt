package com.techuntried.bluetooth.di

import android.content.Context
import com.techuntried.bluetoothshare.data.AndroidBluetoothController
import com.techuntried.bluetoothshare.domain.BluetoothController
import com.techuntried.bluetooth.ui.file.FileBluetoothController
import com.techuntried.bluetooth.ui.file.FileController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }
    @Singleton
    @Provides
    fun providesFileBluetoothController(@ApplicationContext context: Context): FileBluetoothController {
        return FileController(context)
    }

}
