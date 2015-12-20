package com.patson.model

case class LinkConsumptionDetails(linkId : Int, price : LinkPrice, capacity: LinkCapacity, soldSeats: LinkCapacity, fuelCost : Int, crewCost : Int, airportFees: Int, inflightCost : Int, fixedCost : Int, revenue : Int, profit : Int, fromAirportId : Int, toAirportId : Int, airlineId : Int, distance : Int, cycle : Int, var id : Int = 0) extends IdObject