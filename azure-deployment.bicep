@description('Charging Station Service Azure Deployment Template')
param appName string = 'charging-station-service'
param location string = resourceGroup().location
param environment string = 'prod'
param mysqlServerName string = '${appName}-mysql-${uniqueString(resourceGroup().id)}'
param mysqlAdminUser string = 'chargingstation'
@secure()
param mysqlAdminPassword string

// Container Registry
resource containerRegistry 'Microsoft.ContainerRegistry/registries@2023-01-01-preview' = {
  name: '${replace(appName, '-', '')}acr${uniqueString(resourceGroup().id)}'
  location: location
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: true
  }
}

// MySQL Database
resource mysqlServer 'Microsoft.DBforMySQL/flexibleServers@2023-06-30' = {
  name: mysqlServerName
  location: location
  sku: {
    name: 'Standard_B1ms'
    tier: 'Burstable'
  }
  properties: {
    administratorLogin: mysqlAdminUser
    administratorLoginPassword: mysqlAdminPassword
    version: '8.0'
    storage: {
      storageSizeGB: 20
      iops: 360
      autoGrow: 'Enabled'
    }
    backup: {
      backupRetentionDays: 7
      geoRedundantBackup: 'Disabled'
    }
    network: {
      publicNetworkAccess: 'Enabled'
    }
  }
}

// Output values
output webAppUrl string = 'https://${appName}-${uniqueString(resourceGroup().id)}.azurewebsites.net'
output mysqlServerName string = mysqlServer.name
output containerRegistryLoginServer string = containerRegistry.properties.loginServer 