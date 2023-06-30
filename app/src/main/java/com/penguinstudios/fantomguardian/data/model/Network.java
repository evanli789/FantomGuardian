package com.penguinstudios.fantomguardian.data.model;

public enum Network {

    NONE_SELECTED(null, "Select Network", -1, null),
    //TEST_NET("https://fantom-testnet.publicnode.com", "Fantom Testnet", 4002, "https://testnet.ftmscan.com/address/"),
    MAIN_NET("https://rpcapi.fantom.network", "Fantom Mainnet", 250, "https://ftmscan.com/address/");

    private final String baseUrl;
    private final String networkName;
    private final int chainId;
    private final String explorerUrl;

    Network(String baseUrl, String networkName, int chainId, String explorerUrl) {
        this.baseUrl = baseUrl;
        this.networkName = networkName;
        this.chainId = chainId;
        this.explorerUrl = explorerUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getNetworkName() {
        return networkName;
    }

    public int getChainId() {
        return chainId;
    }

    public String getExplorerUrl() {
        return explorerUrl;
    }
}
