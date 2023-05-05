#!/bin/bash

print_usage() {
  printf "Usage:\n"
  printf " %-10s : %s\n" "-f PARAM" "LIQUIBASE_DEFAULTS_FILE"
  printf " %-10s : %s\n" "-s PARAM" "LIQUIBASE_COMMAND_DEFAULT_SCHEMA_NAME"
  printf " %-10s : %s\n" "-o PARAM" "LIQUIBASE_OUTPUT_FILE"
}

optionsCount=0
useExportOption() {
  echo "export $1=\"${2}\""
  export $1="${2}"
  incOptionsCount
}
incOptionsCount() {
  let optionsCount="optionsCount + 1"
}
verifyOptions() {
  if [ $optionsCount == 0 ]
  then
    print_usage
  fi
}

unset LIQUIBASE_DEFAULTS_FILE
unset LIQUIBASE_COMMAND_DEFAULT_SCHEMA_NAME
unset LIQUIBASE_OUTPUT_FILE

OPTIND=1
while getopts 'f:s:o:' flag; do
  case "${flag}" in
    f) useExportOption LIQUIBASE_DEFAULTS_FILE ${OPTARG} ;;
    s) useExportOption LIQUIBASE_COMMAND_DEFAULT_SCHEMA_NAME ${OPTARG} ;;
    o) useExportOption LIQUIBASE_OUTPUT_FILE ${OPTARG} ;;
    *) incOptionsCount; print_usage ;;
  esac
done

verifyOptions
