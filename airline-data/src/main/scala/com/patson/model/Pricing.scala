package com.patson.model
import FlightType._

/**
 * Cost base model
 */
object Pricing {
  val INTERNATIONAL_PRICE_MULTIPLIER = 1.25
  
  //200 km = 150
  //1000 km = 150 + 100 = 250  (800 * 0.125) 
  //2000 km = 250 + 100 = 350  (1000 * 0.1)
  //10000 km = 350 + 400 = 750 (8000 * 0.05)
  val modifierBrackets = List((200, 0.75),(800, 0.125),(1000, 0.1),(Int.MaxValue, 0.05))
  
  def computeStandardPrice(link : Link, linkClass : LinkClass) : Int = {
    computeStandardPrice(link.distance, Computation.getFlightType(link.from, link.to))
  }
  def computeStandardPrice(distance : Int, flightType : FlightType) : Int = {
    var remainDistance = distance
    var price = 0.0
    for (priceBracket <- modifierBrackets if(remainDistance > 0)) {
      if (priceBracket._1 >= remainDistance) {
        price += remainDistance * priceBracket._2
      } else {
        price += priceBracket._1 * priceBracket._2
      }
      remainDistance -= priceBracket._1
    }
    (flightType match {
      case SHORT_HAUL_INTERNATIONAL | LONG_HAUL_INTERNATIONAL | ULTRA_LONG_HAUL_INTERNATIONAL => (price * INTERNATIONAL_PRICE_MULTIPLIER)
      case _ => price
    }).toInt
    
  }
  
  def standardCostAdjustmentRatioFromPrice(link : Link, linkClass :LinkClass, price: Int): Double = {
    standardCostAdjustmentRatioFromPrice(link.distance, Computation.getFlightType(link.from, link.to), linkClass, price)
  }
  // if price is zero, adjustmentRatio = 0 
  // if price is at standard price, adjustmentRatio = 1
  // if price is at double the standard price, adjustmentRatio = 2 . Fair enough!
  def standardCostAdjustmentRatioFromPrice(distance: Int, flightType : FlightType, linkClass :LinkClass, price: Int): Double = {
      var standardPrice = computeStandardPrice(distance, flightType) * linkClass.priceMultiplier
      ((price - standardPrice).toDouble / standardPrice) + 1
  }
}