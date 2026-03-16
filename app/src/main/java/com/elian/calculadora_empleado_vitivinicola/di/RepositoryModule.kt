package com.elian.calculadora_empleado_vitivinicola.di

import com.elian.calculadora_empleado_vitivinicola.repository.SueldosRepository
import com.elian.calculadora_empleado_vitivinicola.repository.SueldosRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSueldosRepository(
        impl: SueldosRepositoryImpl
    ): SueldosRepository
}
