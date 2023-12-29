FROM mcr.microsoft.com/dotnet/sdk:8.0-alpine AS build
WORKDIR .
COPY data-generator/ ./src/
RUN dotnet publish ./src/*.csproj -r linux-musl-x64 -c release -o ./publish/app

FROM mcr.microsoft.com/dotnet/aspnet:8.0-alpine AS base
WORKDIR /app

COPY --from=build /publish/app .

# Add the grate executable
ENV ASPNETCORE_URLS=http://[::]:80
ENV ASPNETCORE_ENVIRONMENT=Production
ENTRYPOINT ["dotnet", "data-generator.dll"]