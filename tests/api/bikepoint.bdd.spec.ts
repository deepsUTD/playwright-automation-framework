import { expect, test } from '@playwright/test';

type AdditionalProperty = {
  key: string;
  value: string;
};

type BikePoint = {
  id: string;
  commonName: string;
  placeType: string;
  additionalProperties: AdditionalProperty[];
};

function getPropertyValues(bikePoint: BikePoint): Map<string, string> {
  return new Map(bikePoint.additionalProperties.map(({ key, value }) => [key.toLowerCase(), value]));
}

test('Given a listed BikePoint, when requesting its details, then location and availability match', async ({ request }) => {
  let selectedBikePoint: BikePoint;
  let bikePointDetails: BikePoint;

  await test.step('Given the BikePoint collection is available', async () => {
    const response = await request.get('https://api.tfl.gov.uk/BikePoint');
    expect(response.status()).toBe(200);

    const bikePoints = (await response.json()) as BikePoint[];
    expect(bikePoints.length).toBeGreaterThan(0);
    selectedBikePoint = bikePoints[0];
  });

  await test.step('When the selected BikePoint is requested by ID', async () => {
    const response = await request.get(
      `https://api.tfl.gov.uk/BikePoint/${encodeURIComponent(selectedBikePoint.id)}`,
    );
    expect(response.status()).toBe(200);
    bikePointDetails = (await response.json()) as BikePoint;
  });

  await test.step('Then the detail response matches the location and has valid availability counts', async () => {
    expect(bikePointDetails.id).toBe(selectedBikePoint.id);
    expect(bikePointDetails.commonName).toBe(selectedBikePoint.commonName);
    expect(bikePointDetails.placeType).toBe('BikePoint');

    const availability = getPropertyValues(bikePointDetails);
    for (const [label, value] of [
      ['NbBikes', availability.get('nbbikes')],
      ['NbDocks', availability.get('nbdocks')],
      ['NbEmptyDocks/NbSpaces', availability.get('nbemptydocks') ?? availability.get('nbspaces')],
    ]) {
      expect(value, `${bikePointDetails.id} is missing ${label}`).toBeDefined();
      const count = Number(value);
      expect(Number.isSafeInteger(count), `${label} must be an integer`).toBe(true);
      expect(count, `${label} must not be negative`).toBeGreaterThanOrEqual(0);
    }
  });
});
