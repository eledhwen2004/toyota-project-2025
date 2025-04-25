class RawRateCalculationScript {
    def calculate(double [] bids,double[] asks) {
        double avarageBid = 0;
        for(double bid : bids){
            avarageBid += bid;
        }
        avarageBid /= bids.length;

        double avarageAsk = 0;
        for(double ask : asks){
            avarageAsk += ask;
        }
        avarageAsk /= asks.length;
        double [] rateFields = new double[2];
        rateFields[0] = avarageBid;
        rateFields[1] = avarageAsk;

        return rateFields;
    }
}

