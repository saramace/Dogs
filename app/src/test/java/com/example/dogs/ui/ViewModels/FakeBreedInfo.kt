package com.example.dogs.ui.ViewModels

import com.example.dogs.api.BreedInfo
import com.example.dogs.api.Life
import com.example.dogs.api.Weight


val fakeBreedInfo = BreedInfo(
    name = "Test Dog",
    description = "A friendly test dog.",
    life = Life(min = 10, max = 12),
    maleWeight = Weight(min = 20, max = 25),
    femaleWeight = Weight(min = 18, max = 22),
    hypoallergenic = false
)