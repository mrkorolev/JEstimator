package com.company;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.company.interfaces.Actions;
import lombok.Data;

public class Main {

    public static Args arguments;
    public static String method;

    @Data
    public static class Args{

        @Parameter(names = {"-h", "--help"}, description = "Get description of usage", help = true, order = 0)
        private boolean help;

        @Parameter(names = {"-dp", "--decimal-places"}, description = "Numer of decimal places used for values in normal table", order = 1, required = true)
        private int decimalPlaces;

        @Parameter(names = {"-divs", "--divisions"}, description = "Number of divisions (delta x) used in every numerical method", order = 2, required = true)
        private int divisions;

        @Parameter(names = {"-rct", "--rectangular"}, description = "Use rectangular numerical method for normal table generation", order = 3)
        private boolean rectangular;

        @Parameter(names = {"-tr", "--trapezium"}, description = "Use trapezium numerical method for normal table generation", order = 3)
        private boolean trapezium;

        @Parameter(names = {"-smp", "--simpson"}, description = "Use Simpson's numerical method for normal table generation", order = 3)
        private boolean simpson;
    }

    public static void main(String[] args) {
        Args opts = new Args();
        arguments = opts;
        JCommander jc = JCommander.newBuilder().addObject(opts).build();
        jc.parse(args);
        Actions.analyzeCmdArgs(jc, opts, args);
    }
}
