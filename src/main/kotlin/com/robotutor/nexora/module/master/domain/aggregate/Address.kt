package com.robotutor.nexora.module.master.domain.aggregate

import com.robotutor.nexora.module.master.domain.vo.City
import com.robotutor.nexora.module.master.domain.vo.Country
import com.robotutor.nexora.module.master.domain.vo.District
import com.robotutor.nexora.module.master.domain.vo.PinCode
import com.robotutor.nexora.module.master.domain.vo.State
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.Event

data class Address(
    val pinCode: PinCode,
    val city: City,
    val district: District,
    val state: State,
    val country: Country,
) : AggregateRoot<Address, PinCode, Event>(pinCode)
