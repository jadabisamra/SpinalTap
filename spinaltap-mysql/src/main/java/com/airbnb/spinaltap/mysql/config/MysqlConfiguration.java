/**
 * Copyright 2018 Airbnb. Licensed under Apache-2.0. See License in the project root for license
 * information.
 */
package com.airbnb.spinaltap.mysql.config;

import com.airbnb.spinaltap.common.config.DestinationConfiguration;
import com.airbnb.spinaltap.mysql.BinlogFilePos;
import com.airbnb.spinaltap.mysql.MysqlSource;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

/** Configuration for a Mysql Source */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class MysqlConfiguration extends AbstractMysqlConfiguration {
  public static final String TYPE = "mySQL";
  public static final String INSTANCE_TAG = TYPE.toLowerCase();
  public static final HostRole DEFAULT_HOST_ROLE = HostRole.MASTER;
  public static final int DEFAULT_SOCKET_TIMEOUT_IN_SECONDS = 90;
  public static final int DEFAULT_PORT = 5672;
  public static final boolean DEFAULT_SCHEMA_VERSION_ENABLED = false;
  public static final boolean DEFAULT_LARGE_MESSAGE_ENABLED = false;
  public static final long DEFAULT_DELAY_SEND_MS = 0L;
  public static final Map<HostRole, String> MYSQL_TOPICS =
      ImmutableMap.of(
          MysqlConfiguration.HostRole.MASTER, "spinaltap",
          MysqlConfiguration.HostRole.REPLICA, "spinaltap_mysql_replica",
          MysqlConfiguration.HostRole.MIGRATION, "spinaltap_mysql_migration");

  public MysqlConfiguration(
      String name,
      List<String> canonicalTableNames,
      String host,
      String hostRole,
      int port,
      DestinationConfiguration destinationConfiguration) {
    super(name, TYPE, INSTANCE_TAG, destinationConfiguration);

    this.canonicalTableNames = canonicalTableNames;
    this.host = host;
    this.port = port;

    if (!Strings.isNullOrEmpty(hostRole)) {
      this.hostRole = HostRole.valueOf(hostRole.toUpperCase());
    }
  }

  public MysqlConfiguration() {
    super(TYPE, INSTANCE_TAG);
  }

  @NotEmpty
  @JsonProperty("tables")
  private List<String> canonicalTableNames;

  @NotNull @JsonProperty private String host;

  @JsonProperty("host_role")
  private HostRole hostRole = DEFAULT_HOST_ROLE;

  @Min(1)
  @Max(65535)
  @JsonProperty
  private int port = DEFAULT_PORT;

  @JsonProperty("socket_timeout_seconds")
  private int socketTimeoutInSeconds = DEFAULT_SOCKET_TIMEOUT_IN_SECONDS;

  @JsonProperty("schema_version_enabled")
  private boolean schemaVersionEnabled = DEFAULT_SCHEMA_VERSION_ENABLED;

  @JsonProperty("initial_binlog_position")
  private BinlogFilePos initialBinlogFilePosition = MysqlSource.LATEST_BINLOG_POS;

  @JsonProperty("large_message_enabled")
  private boolean largeMessageEnabled = DEFAULT_LARGE_MESSAGE_ENABLED;

  @JsonProperty("delay_send_ms")
  private long delaySendMs = DEFAULT_DELAY_SEND_MS;

  @Override
  public void setPartitions(int partitions) {
    // We only support 1 partition for mysql sources
  }

  public enum HostRole {
    MASTER,
    REPLICA,
    MIGRATION
  }
}
