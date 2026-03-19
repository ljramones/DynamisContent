module org.dynamisengine.content.core {
    requires transitive org.dynamisengine.content.api;
    requires org.dynamisengine.core;
    requires com.fasterxml.jackson.databind;

    exports org.dynamisengine.content.core;
    exports org.dynamisengine.content.core.cache;
    exports org.dynamisengine.content.core.loader;
    exports org.dynamisengine.content.core.manifest;
}
