import { expect, test } from '@playwright/test';

const bikePointEndpoint = 'https://api.tfl.gov.uk/BikePoint';

type AdditionalProperty = {
  key: string;
  value: string;
};

type BikePoint = {
  id: string;
  commonName: string;
  placeType: string;
  lat: number;
  lon: number;
  additionalProperties: AdditionalProperty[];
};

function propertyValues(place: BikePoint): Map<string, string> {
  return new Map(place.additionalProperties.map(({ key, value }) => [key.toLowerCase(), value]));
}

function expectCount(value: string | undefined, propertyName: string, bikePointId: string): void {
  expect(value, `${bikePointId} is missing ${propertyName}`).toBeDefined();
  const count = Number(value);
  expect(Number.isSafeInteger(count), `${bikePointId} has a non-integer ${propertyName}`).toBe(true);
  expect(count, `${bikePointId} has a negative ${propertyName}`).toBeGreaterThanOrEqual(0);
}

test.describe('TfL BikePoint API', () => {
  test('GET /BikePoint returns a collection of bike point locations', async ({ request }) => {
    const response = await request.get(bikePointEndpoint);

    expect(response.status()).toBe(200);
    expect(response.headers()['content-type']).toContain('application/json');

    const bikePoints = (await response.json()) as BikePoint[];
    expect(Array.isArray(bikePoints)).toBe(true);
    expect(bikePoints.length).toBeGreaterThan(0);

    for (const bikePoint of bikePoints) {
      expect(bikePoint.id).toEqual(expect.any(String));
      expect(bikePoint.id.length).toBeGreaterThan(0);
      expect(bikePoint.commonName).toEqual(expect.any(String));
      expect(bikePoint.placeType).toBe('BikePoint');
      expect(Array.isArray(bikePoint.additionalProperties)).toBe(true);
      expect(bikePoint.lat).toEqual(expect.any(Number));
      expect(bikePoint.lat).toBeGreaterThanOrEqual(-90);
      expect(bikePoint.lat).toBeLessThanOrEqual(90);
      expect(bikePoint.lon).toEqual(expect.any(Number));
      expect(bikePoint.lon).toBeGreaterThanOrEqual(-180);
      expect(bikePoint.lon).toBeLessThanOrEqual(180);
    }
  });

  test('availability properties contain non-negative counts', async ({ request }) => {
    const response = await request.get(bikePointEndpoint);
    expect(response.status()).toBe(200);

    const bikePoints = (await response.json()) as BikePoint[];
    expect(bikePoints.length).toBeGreaterThan(0);

    for (const bikePoint of bikePoints) {
      const values = propertyValues(bikePoint);
      expectCount(values.get('nbbikes'), 'NbBikes', bikePoint.id);
      expectCount(values.get('nbdocks'), 'NbDocks', bikePoint.id);
      expectCount(
        values.get('nbspaces') ?? values.get('nbemptydocks'),
        'NbSpaces/NbEmptyDocks',
        bikePoint.id,
      );
    }
  });

  test('GET /BikePoint/Search returns 400 when the required query is empty', async ({ request }) => {
    const response = await request.get(`${bikePointEndpoint}/Search`, {
      params: { query: '' },
    });

    expect(response.status()).toBe(400);
    const error = await response.json();
    expect(error.httpStatusCode).toBe(400);
    expect(error.message).toContain('A value is required');
  });

  test('GET /BikePoint/{id} returns 404 for an unknown BikePoint ID', async ({ request }) => {
    const response = await request.get(`${bikePointEndpoint}/BikePoints_DOES_NOT_EXIST_987654321`);

    expect(response.status()).toBe(404);
    const error = await response.json();
    expect(error.httpStatusCode).toBe(404);
    expect(error.httpStatus).toBe('NotFound');
  });
});