This is the first release of codename **Serval Project**.

It uses pseudo-randomly generated [JSON data](https://api.splork.de/dummy_data.json) from [Markus Sommer](https://github.com/CryptoCopter) with the following structure:

- measurements
  - type: radiation|temperature
  - value: int
- location
  - latitude: double
  - longitude: double
  - geohash: String
- time: long

Working features:

- data fetching
- data overview
- basic map functionalities

Be prepared for the following features coming in a release at the beginning of september 2017:

- dashboard
- advanced map
- fancy UI/UX
- bugs