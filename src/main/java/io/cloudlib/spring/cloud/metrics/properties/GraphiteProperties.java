package io.cloudlib.spring.cloud.metrics.properties;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix="graphite")
public class GraphiteProperties {
    private boolean enabled;
    private String hostname;
    private int port;
    private long period;
    private String prefix;
    private String appprefix;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
	 * @return the appprefix
	 */
    @Bean
	public String getAppprefix() {
		return appprefix;
	}

	/**
	 * @param appprefix the appprefix to set
	 */
	public void setAppprefix(String appprefix) {
		this.appprefix = appprefix;
	}

	@Override
    public String toString(){
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
