CREATE TABLE IF NOT EXISTS ${tablePrefix}cooldowns (
      type TINYTEXT NOT NULL,
      uuid ${uuidType} NOT NULL,
      "time" TIMESTAMP NOT NULL
)${tableDefaults};