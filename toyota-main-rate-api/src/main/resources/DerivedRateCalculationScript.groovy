class DerivedRateCalculationScript{
    def calculate(double []firstRateBids,double []firstRateAsks,double []secondRateBids,double []secondRateAsks){
        double askSum = 0.0
        double bidSum = 0.0
        for (double firstRateAsk : firstRateAsks) {
            askSum += firstRateAsk
        }
        askSum /= firstRateAsks.length

        for (double firstRateBid : firstRateBids) {
            bidSum += firstRateBid
        }
        bidSum /= firstRateBids.length

        double mid = (askSum + bidSum) / 2.0

        double derivedRateBid = 0.0
        for (double secondRateBid : secondRateBids) {
            derivedRateBid += secondRateBid
        }
        derivedRateBid = mid * (derivedRateBid / secondRateBids.length)

        double derivedRateAsk = 0.0
        for (double secondRateAsk : secondRateAsks) {
            derivedRateAsk += secondRateAsk
        }
        derivedRateAsk = mid * (derivedRateAsk / secondRateAsks.length)

        double [] derivedRateFields = new double[2]
        derivedRateFields[0] = derivedRateBid
        derivedRateFields[1] =  derivedRateAsk
        return derivedRateFields
    }
}

