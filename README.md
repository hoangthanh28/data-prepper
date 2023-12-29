Step to reproduce the bugs:
```sh
docker-compose -f docker-compose.yml build && docker-compose -f docker-compose.yml up -d
```

## Then send the request to data generator
```curl
curl --location 'http://localhost:5000'
```

## Error
2023-12-29T07:28:02,516 [pool-13-thread-1] ERROR org.opensearch.dataprepper.plugins.source.otellogs.OTelLogsGrpcService - Failed to parse the request resource_logs {
  resource {
    ...truncate...
  scope_logs {
    scope {
      name: "Microsoft.Extensions.Hosting.Internal.Host"
    }
    log_records {
      time_unix_nano: 1703834880595497100
      severity_number: SEVERITY_NUMBER_DEBUG
      severity_text: "Debug"
      body {
        string_value: "Connection id \"{ConnectionId}\" sending FIN because: \"{Reason}\""
      }
      attributes {
        key: "ConnectionId"
        value {
          string_value: "0HN087UNTCNA9"
        }
      }
      attributes {
        key: "Reason"
        value {
          string_value: "The Socket transport\'s send loop completed gracefully."
        }
      }
      attributes {
        key: "ConnectionId"
        value {
          string_value: "0HN087UNTCNA9"
        }
      }
      observed_time_unix_nano: 1703834880595497100
    }
  }
}
 due to:
java.lang.IllegalStateException: Duplicate key log.attributes.ConnectionId (attempted merging values 0HN087UNTCNA9 and 0HN087UNTCNA9)
	at java.base/java.util.stream.Collectors.duplicateKeyException(Collectors.java:135) ~[?:?]
	at java.base/java.util.stream.Collectors.lambda$uniqKeysMapAccumulator$1(Collectors.java:182) ~[?:?]
	at java.base/java.util.stream.ReduceOps$3ReducingSink.accept(ReduceOps.java:169) ~[?:?]
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499) ~[?:?]
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[?:?]
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682) ~[?:?]
	at org.opensearch.dataprepper.plugins.otel.codec.OTelProtoCodec.unpackKeyValueListLog(OTelProtoCodec.java:1094) ~[otel-proto-common-2.6.1.jar:?]
	at org.opensearch.dataprepper.plugins.otel.codec.OTelProtoCodec$OTelProtoDecoder.lambda$processLogsList$7(OTelProtoCodec.java:376) ~[otel-proto-common-2.6.1.jar:?]
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197) ~[?:?]
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499) ~[?:?]
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[?:?]
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682) ~[?:?]
	at org.opensearch.dataprepper.plugins.otel.codec.OTelProtoCodec$OTelProtoDecoder.processLogsList(OTelProtoCodec.java:390) ~[otel-proto-common-2.6.1.jar:?]
	at org.opensearch.dataprepper.plugins.otel.codec.OTelProtoCodec$OTelProtoDecoder.lambda$parseResourceLogs$3(OTelProtoCodec.java:212) ~[otel-proto-common-2.6.1.jar:?]
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197) ~[?:?]
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499) ~[?:?]
	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(StreamSpliterators.java:310) ~[?:?]
	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Streams.java:735) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499) ~[?:?]
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[?:?]
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682) ~[?:?]
	at org.opensearch.dataprepper.plugins.otel.codec.OTelProtoCodec$OTelProtoDecoder.parseResourceLogs(OTelProtoCodec.java:219) ~[otel-proto-common-2.6.1.jar:?]
	at org.opensearch.dataprepper.plugins.otel.codec.OTelProtoCodec$OTelProtoDecoder.lambda$parseExportLogsServiceRequest$1(OTelProtoCodec.java:191) ~[otel-proto-common-2.6.1.jar:?]
	at java.base/java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:273) ~[?:?]
	at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1625) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:499) ~[?:?]
	at java.base/java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:921) ~[?:?]
	at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[?:?]
	at java.base/java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:682) ~[?:?]
	at org.opensearch.dataprepper.plugins.otel.codec.OTelProtoCodec$OTelProtoDecoder.parseExportLogsServiceRequest(OTelProtoCodec.java:191) ~[otel-proto-common-2.6.1.jar:?]
	at org.opensearch.dataprepper.plugins.source.otellogs.OTelLogsGrpcService.processRequest(OTelLogsGrpcService.java:87) ~[otel-logs-source-2.6.1.jar:?]
	at org.opensearch.dataprepper.plugins.source.otellogs.OTelLogsGrpcService.lambda$export$0(OTelLogsGrpcService.java:80) ~[otel-logs-source-2.6.1.jar:?]
	at io.micrometer.core.instrument.composite.CompositeTimer.record(CompositeTimer.java:141) ~[micrometer-core-1.11.3.jar:1.11.3]
	at org.opensearch.dataprepper.plugins.source.otellogs.OTelLogsGrpcService.export(OTelLogsGrpcService.java:80) ~[otel-logs-source-2.6.1.jar:?]
	at io.opentelemetry.proto.collector.logs.v1.LogsServiceGrpc$MethodHandlers.invoke(LogsServiceGrpc.java:246) ~[opentelemetry-proto-0.16.0-alpha.jar:0.16.0]
	at io.grpc.stub.ServerCalls$UnaryServerCallHandler$UnaryServerCallListener.onHalfClose(ServerCalls.java:182) ~[grpc-stub-1.57.2.jar:1.57.2]
	at com.linecorp.armeria.internal.server.grpc.AbstractServerCall.invokeOnMessage(AbstractServerCall.java:387) ~[armeria-grpc-1.25.2.jar:?]
	at com.linecorp.armeria.internal.server.grpc.AbstractServerCall.lambda$onRequestMessage$2(AbstractServerCall.java:351) ~[armeria-grpc-1.25.2.jar:?]
	at com.linecorp.armeria.internal.shaded.guava.util.concurrent.SequentialExecutor$1.run(SequentialExecutor.java:125) [armeria-1.25.2.jar:?]
	at com.linecorp.armeria.internal.shaded.guava.util.concurrent.SequentialExecutor$QueueWorker.workOnQueue(SequentialExecutor.java:237) [armeria-1.25.2.jar:?]
	at com.linecorp.armeria.internal.shaded.guava.util.concurrent.SequentialExecutor$QueueWorker.run(SequentialExecutor.java:182) [armeria-1.25.2.jar:?]
	at com.linecorp.armeria.common.DefaultContextAwareRunnable.run(DefaultContextAwareRunnable.java:45) [armeria-1.25.2.jar:?]
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539) [?:?]
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264) [?:?]
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304) [?:?]
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136) [?:?]
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635) [?:?]
	at java.base/java.lang.Thread.run(Thread.java:840) [?:?]
