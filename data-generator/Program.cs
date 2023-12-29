using OpenTelemetry.Logs;
using OpenTelemetry.Resources;

var builder = WebApplication.CreateBuilder(args);

builder.Logging.AddOpenTelemetry(logging =>
{
    logging.IncludeScopes = true;

    var resourceBuilder = ResourceBuilder
        .CreateDefault()
        .AddService(builder.Environment.ApplicationName);

    logging.SetResourceBuilder(resourceBuilder)
        .AddOtlpExporter();
});

var app = builder.Build();

app.MapGet("/", (ILogger<Program> logger) =>
{
    return "Hello from OpenTelemetry Logs!";
});

app.Run();
