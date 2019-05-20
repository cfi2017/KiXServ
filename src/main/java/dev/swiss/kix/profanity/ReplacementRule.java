package dev.swiss.kix.profanity;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@SerializableAs("ReplacementRule")
public class ReplacementRule implements Cloneable, ConfigurationSerializable {

    private String from;
    private String to;

    public ReplacementRule(String from, String to) {
        this.from = from;
        if (to == null) this.to = "";
        else this.to = to;
    }

    public String apply(String message) {
        return message.replaceAll(from, to);
    }

    private String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    private String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplacementRule that = (ReplacementRule) o;
        return getFrom().equals(that.getFrom()) &&
                getTo().equals(that.getTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo());
    }

    @Override
    public String toString() {
        return "ReplacementRule{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("from", this.from);
        result.put("to", this.to);
        return result;
    }

    public static ReplacementRule deserialize(Map<String, Object> args) {
        if (args.containsKey("from") && args.containsKey("to")) {
            return new ReplacementRule((String)args.get("from"), (String)args.get("to"));
        }  return null;
    }

}
