#!/usr/bin/env groovy

def call(Map parameters) {
  List options = []

  if (parameters.extra) {
      options += parameters.extra
  }

  options.add([$class: 'BuildDiscarderProperty',
             strategy: [
               $class: 'LogRotator', 
               daysToKeepStr: '90',
               numToKeepStr: '10',
               artifactDaysToKeepStr: '90',
               artifactNumToKeepStr: '50'
  ]])

  properties(options)
}

def call(List extra = []) {
    call(extra: extra)
}
