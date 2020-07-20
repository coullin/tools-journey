package org.apframework.siddhi;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.query.output.callback.QueryCallback;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.util.EventPrinter;
import org.apframework.siddhi.util.CustomFunctionExtension;

/**
 * The sample demonstrate how to use custom UDFs in Siddhi within another Java program.
 */
public class ExtensionSample {

    public static void main(String[] args) throws InterruptedException {

        // Creating Siddhi Manager
        SiddhiManager siddhiManager = new SiddhiManager();

        //Register the extension to Siddhi Manager
        siddhiManager.setExtension("custom:plus", CustomFunctionExtension.class);

        //Siddhi Application
        String siddhiApp = "" +
                "define stream StockStream (symbol string, price long, volume long);" +
                "" +
                "@info(name = 'query1') " +
                "from StockStream " +
                "select symbol , custom:plus(price, volume) as totalCount " +
                "insert into Output;";

        //Generating runtime
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(siddhiApp);

        //Adding callback to retrieve output events from query
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timestamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timestamp, inEvents, removeEvents);
            }
        });

        //Retrieving InputHandler to push events into Siddhi
        InputHandler inputHandler = siddhiAppRuntime.getInputHandler("StockStream");

        //Starting event processing
        siddhiAppRuntime.start();

        //Sending events to Siddhi
        inputHandler.send(new Object[]{"IBM", 700L, 100L});
        inputHandler.send(new Object[]{"WSO2", 600L, 200L});
        inputHandler.send(new Object[]{"GOOG", 60L, 200L});
        Thread.sleep(500);

        //Shutting down the runtime
        siddhiAppRuntime.shutdown();

        //Shutting down Siddhi
        siddhiManager.shutdown();

    }
}
