package com.marinov.watchweather.ui

import com.marinov.watchweather.R
import com.marinov.watchweather.data.model.City
import com.marinov.watchweather.data.model.DataSource

object CityUiBinder {

    val buttonToCity: Map<Int, City> = mapOf(
        // Climatempo
        R.id.btn_sao_paulo to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/558/saopaulo-sp",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_ubatuba to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/570/ubatuba-sp",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_rio_de_janeiro to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/321/riodejaneiro-rj",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_florianopolis to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/377/florianopolis-sc",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_curitiba to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/271/curitiba-pr",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_praia_grande to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/523/praiagrande-sp",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_belo_horizonte to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/107/belohorizonte-mg",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_porto_alegre to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/363/portoalegre-rs",
            DataSource.CLIMATEMPO
        ),
        R.id.btn_belem to City(
            "https://www.climatempo.com.br/previsao-do-tempo/agora/cidade/232/belem-pa",
            DataSource.CLIMATEMPO
        ),

        // Weather.com
        R.id.btn_taipei to City(
            "https://weather.com/pt-BR/clima/hoje/l/ce745664a88b5afafeb9b67d548ae1f5cfbcc2631f3cb119d921b2c8ca7a20be",
            DataSource.WEATHER_COM
        ),
        R.id.btn_hong_kong to City(
            "https://weather.com/pt-BR/clima/hoje/l/5e9589bf5805da723141e8f56378fadcd6dfcb3a72844e124acfb4c61620cc15",
            DataSource.WEATHER_COM
        ),
        R.id.btn_shenzhen to City(
            "https://weather.com/pt-BR/clima/hoje/l/1670890a44c4bb7d7d66e47d0e79a8bfea1474ab51e9dd771062ab4a959e0c8a",
            DataSource.WEATHER_COM
        ),
        R.id.btn_pequim to City(
            "https://weather.com/pt-BR/clima/hoje/l/00c189c38a25908eae6f4246e0d4648890e75239d4331f4517f1fc38e920ff05",
            DataSource.WEATHER_COM
        ),
        R.id.btn_nova_deli to City(
            "https://weather.com/pt-BR/clima/hoje/l/7d67733d29d6a86f79bd6a172edd760e933c9f65987b7cc68362088d1e4afeec",
            DataSource.WEATHER_COM
        ),
        R.id.btn_sao_petersburgo to City(
            "https://weather.com/pt-BR/clima/hoje/l/866cb3be4344b5593f0928d35c0ee0cda69d89da7799fac8fda841adabd82a17",
            DataSource.WEATHER_COM
        ),
        R.id.btn_kiev to City(
            "https://weather.com/pt-BR/clima/hoje/l/88b31cb7a7d9b9c086599aebf8d87a2333cd69eae36776c3267eee41592a3e91",
            DataSource.WEATHER_COM
        ),
        R.id.btn_doha to City(
            "https://weather.com/pt-BR/clima/hoje/l/7dafe8e2c513aaed09df2c0038b86b2b64c308064bda1c3509231c0a513a1ebb",
            DataSource.WEATHER_COM
        ),
        R.id.btn_berlim to City(
            "https://weather.com/pt-BR/clima/hoje/l/2355888e52076fe39a2616d1423d554c76647d9c2f261e5b583918435bf6c42b",
            DataSource.WEATHER_COM
        ),
        R.id.btn_frankfurt to City(
            "https://weather.com/pt-BR/clima/hoje/l/55298a88f04ef9534dac59e109a1f147287cd50b1c09c59a9087e33a5d148eb2",
            DataSource.WEATHER_COM
        ),
        R.id.btn_paris to City(
            "https://weather.com/pt-BR/clima/hoje/l/a5c27da38afe789545e3446ede0bfd5042030764469a6cd4fff4e9468c74d2a7",
            DataSource.WEATHER_COM
        ),
        R.id.btn_lisboa to City(
            "https://weather.com/pt-BR/clima/hoje/l/071f551f4ce6727dc75e15dfacfd92c56a0f0fcdb0e41a60e128ae319be9463c",
            DataSource.WEATHER_COM
        ),
        R.id.btn_porto to City(
            "https://weather.com/pt-BR/clima/hoje/l/cd086140a970cbea1fa9ca908b2a87339076a7615de814fbf85488835ff8a8ef",
            DataSource.WEATHER_COM
        ),
        R.id.btn_roma to City(
            "https://weather.com/pt-BR/clima/hoje/l/104b5c3a7e17868e40f84026b44fd565a02ee18193bb030a5cbd3076e58c01bc",
            DataSource.WEATHER_COM
        ),
        R.id.btn_bruxelas to City(
            "https://weather.com/pt-BR/clima/hoje/l/d1ecabe854ff08676c7162ab00afc13a714c887bb2c9df889c1e0fc7309e3c1c",
            DataSource.WEATHER_COM
        ),
        R.id.btn_amsterdam to City(
            "https://weather.com/pt-BR/clima/hoje/l/253b712e01bcec0d92d5db5aa2c0b5699a1e14c50e72ca007ccc583178bd30a3",
            DataSource.WEATHER_COM
        ),
        R.id.btn_seul to City(
            "https://weather.com/pt-BR/clima/hoje/l/bcf9c3c268f09aa5353d6d374302bd3c826ea47577a25f549d7e8997f6e6da45",
            DataSource.WEATHER_COM
        ),
        R.id.btn_pyongyang to City(
            "https://weather.com/pt-BR/clima/hoje/l/da1b117b5877237fe1daee5687d6d55fb6fd53a18c3e81dda29009a36469418c",
            DataSource.WEATHER_COM
        ),
        R.id.btn_madri to City(
            "https://weather.com/pt-BR/clima/hoje/l/f620d7fe58f453124aa71caa578d94f09a298b74f2e9bd519413ad3d9ce6a771",
            DataSource.WEATHER_COM
        ),
        R.id.btn_havana to City(
            "https://weather.com/pt-BR/clima/hoje/l/eb2d56dee45d2ba7384ff99a47a70f3a5c64d9a331a8b059a7b331dc62579b33",
            DataSource.WEATHER_COM
        ),
        R.id.btn_santiago to City(
            "https://weather.com/pt-BR/clima/hoje/l/9d3b491220ea48e9a1cfe014e32934278032da27c04f7e964c6e65ec4de028e8",
            DataSource.WEATHER_COM
        ),
        R.id.btn_cidade_do_mexico to City(
            "https://weather.com/pt-BR/clima/hoje/l/279b9b5f79fa02ab0d2133430d64bbb06bb53ae0fdf3234ea59d68dba44812cb",
            DataSource.WEATHER_COM
        ),
        R.id.btn_caracas to City(
            "https://weather.com/pt-BR/clima/hoje/l/1eb2f9b8e20c3fb53805ed92c5f198993d2615356fd5b46a104e434e23b6f56e",
            DataSource.WEATHER_COM
        ),
        R.id.btn_sucre to City(
            "https://weather.com/pt-BR/clima/hoje/l/5d6fd655f00f9b39b6b89003aa7dc5230d9428dea70a5217cf24e432efb79f23",
            DataSource.WEATHER_COM
        ),
        R.id.btn_assuncao to City(
            "https://weather.com/pt-BR/clima/hoje/l/79579780a17cbe68d766d5075b6082e69a76eb017be29ebbad9698b718efdf96",
            DataSource.WEATHER_COM
        ),
        R.id.btn_montevideu to City(
            "https://weather.com/pt-BR/clima/hoje/l/0ec0b65ad1873426e3c3fb399b2c7f662b6fe0fd1e6e05b2e05c29bf9673551e",
            DataSource.WEATHER_COM
        ),
        R.id.btn_buenos_aires to City(
            "https://weather.com/pt-BR/clima/hoje/l/2447e41747ecf3ceead70ef4a5d89867e3a9cb3f1e92b0ddfce326652eb6c46e",
            DataSource.WEATHER_COM
        ),
        R.id.btn_varsovia to City(
            "https://weather.com/pt-BR/clima/hoje/l/c40c3d36ef5e02c7bfb64f4f9dde3c3783248c911f7cc83bf1345d4907336463",
            DataSource.WEATHER_COM
        ),
        R.id.btn_toquio to City(
            "https://weather.com/pt-BR/clima/hoje/l/a2d49e31992a3679724161c3a9029211ecefeda3e1da24489637a5685ded3fdf",
            DataSource.WEATHER_COM
        ),
        R.id.btn_koto to City(
            "https://weather.com/pt-BR/clima/hoje/l/3f7a1b4c1033eea2e1e647a686a9e488cf9dd32cc9f3402d3c9c06c847de3093",
            DataSource.WEATHER_COM
        ),
        R.id.btn_abu_dhabi to City(
            "https://weather.com/pt-BR/clima/hoje/l/3d5d25f87ba096ee5d2460f4b279363402a1f31068b3d0674059f68ad94821da",
            DataSource.WEATHER_COM
        ),
        R.id.btn_teera to City(
            "https://weather.com/pt-BR/clima/hoje/l/75814d99326fdea4d4f0d79772a0a7193bb0b4adf556bae671e29816c12a7ba1",
            DataSource.WEATHER_COM
        ),
        R.id.btn_cankaya to City(
            "https://weather.com/pt-BR/clima/hoje/l/7f6d21f7b08cf5eb15d1371590e895e74304f3972a925eacf3fc914bec2c7464",
            DataSource.WEATHER_COM
        ),
        R.id.btn_chicago to City(
            "https://weather.com/weather/today/l/4c9ff75840c6ce23fa10812d0f14b605af47896e9ca3fd59abdb9edd1b9d486a",
            DataSource.WEATHER_COM
        ),
        R.id.btn_new_york_city to City(
            "https://weather.com/weather/today/l/98e8083bb7de0fc467fd1e22a1692f8f200343e4e0acc3b3fc31e71d29113b54",
            DataSource.WEATHER_COM
        ),
        R.id.btn_texas_city to City(
            "https://weather.com/weather/today/l/5405e8a4f7b8d4577e185e7051998f2613bf2bca1d8f463cb2bb50520501f55b",
            DataSource.WEATHER_COM
        ),
        R.id.btn_glover_park to City(
            "https://weather.com/weather/today/l/8159437285b84ab54f05db440efcfae11aff64b5fdc0f8ed60f696c7e59d6166",
            DataSource.WEATHER_COM
        ),
        R.id.btn_washington_dc to City(
            "https://weather.com/weather/today/l/c4025fdf0177c872b7fc2e0d09e7c523995ef49e21145e2d2438fe08649ca9e8",
            DataSource.WEATHER_COM
        ),
        R.id.btn_florida_center to City(
            "https://weather.com/weather/today/l/72499e4e966f535761c206b7d7c176520f961080e264ce481c2de224f9f651a5",
            DataSource.WEATHER_COM
        ),
        R.id.btn_california_pa to City(
            "https://weather.com/weather/today/l/11e2a661192a085e9f8fcb37c71bb3007f2d52e89989ed75047db1a5ae2c6d7f",
            DataSource.WEATHER_COM
        ),
        R.id.btn_jerusalem to City(
            "https://weather.com/pt-BR/clima/hoje/l/eb9298942ea6bff5eefab6cba776eb07740f5a3a8f9385bff8ed65798f9f86eb",
            DataSource.WEATHER_COM
        )
    )
}