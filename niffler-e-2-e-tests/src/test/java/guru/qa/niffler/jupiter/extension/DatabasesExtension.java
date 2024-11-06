package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.template.Connections;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
    }
}
