package org.dynamiscontent.core.manifest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dynamis.core.exception.DynamisException;
import org.dynamiscontent.api.id.AssetId;
import org.dynamiscontent.api.manifest.AssetManifest;
import org.dynamiscontent.api.manifest.ManifestEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class JsonAssetManifestReader {

    private final ObjectMapper mapper = new ObjectMapper();

    public AssetManifest read(Path jsonFile) {
        Objects.requireNonNull(jsonFile, "jsonFile");

        JsonNode root;
        try {
            root = mapper.readTree(jsonFile.toFile());
        } catch (IOException e) {
            throw new DynamisException("Failed to read manifest JSON: " + jsonFile, e);
        }

        JsonNode versionNode = required(root, "version", jsonFile);
        if (!versionNode.isInt() || versionNode.asInt() < 1) {
            throw new DynamisException("Manifest version must be an integer >= 1: " + jsonFile);
        }

        JsonNode entriesNode = required(root, "entries", jsonFile);
        if (!entriesNode.isArray()) {
            throw new DynamisException("Manifest entries must be an array: " + jsonFile);
        }

        Map<AssetId, ManifestEntry> entries = new HashMap<>();
        for (JsonNode node : entriesNode) {
            String idText = requiredText(node, "id", jsonFile);
            String typeId = requiredText(node, "typeId", jsonFile);
            String uri = requiredText(node, "uri", jsonFile);

            List<AssetId> dependencies = new ArrayList<>();
            JsonNode depsNode = node.get("dependencies");
            if (depsNode != null && !depsNode.isNull()) {
                if (!depsNode.isArray()) {
                    throw new DynamisException("Manifest dependencies must be an array: " + jsonFile);
                }
                for (JsonNode depNode : depsNode) {
                    if (!depNode.isTextual() || depNode.asText().isBlank()) {
                        throw new DynamisException("Manifest dependency must be a non-blank string: " + jsonFile);
                    }
                    dependencies.add(AssetId.of(depNode.asText()));
                }
            }

            AssetId id = AssetId.of(idText);
            entries.put(id, new ManifestEntry(id, typeId, uri, dependencies));
        }

        return new AssetManifest(entries);
    }

    private static JsonNode required(JsonNode root, String field, Path file) {
        JsonNode node = root.get(field);
        if (node == null || node.isNull()) {
            throw new DynamisException("Manifest missing required field '" + field + "': " + file);
        }
        return node;
    }

    private static String requiredText(JsonNode root, String field, Path file) {
        JsonNode node = required(root, field, file);
        if (!node.isTextual() || node.asText().isBlank()) {
            throw new DynamisException("Manifest field '" + field + "' must be a non-blank string: " + file);
        }
        return node.asText();
    }
}
