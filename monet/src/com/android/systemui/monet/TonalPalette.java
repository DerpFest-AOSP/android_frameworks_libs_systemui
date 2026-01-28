/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.monet;

import com.android.internal.graphics.cam.Cam;
import com.android.internal.graphics.cam.Frame;
import com.android.internal.graphics.ColorUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TonalPalette {
    private final float mHue;
    private final float mChroma;
    private final Map<Integer, Integer> cache = new HashMap<>();
    /**
     * @deprecated Do not use. For color system only
     */
    @Deprecated
    public final List<Integer> allShades;
    public final Map<Integer, Integer> allShadesMapped;

    TonalPalette(float hue, float chroma) {
        this(hue, chroma, 1f, 1f);
    }

    TonalPalette(float hue, float chroma, float luminanceFactor, float chromaFactor) {
        this.mHue = hue;
        this.mChroma = chroma * chromaFactor;
        this.allShades = SHADE_KEYS.stream().map(key -> getAtTone(key.floatValue(), luminanceFactor))
                .collect(Collectors.toList());
        this.allShadesMapped = SHADE_KEYS.stream().collect(
                Collectors.toMap(key -> key, key -> getAtTone(key.floatValue(), luminanceFactor)));
    }

    /**
     * Create tones using the CAM hue and chroma from a color.
     *
     * @param argb ARGB representation of a color
     * @return Tones matching that color's hue and chroma.
     */
    public static TonalPalette fromInt(int argb) {
        Cam cam = Cam.fromInt(argb);
        return new TonalPalette(cam.getHue(), cam.getChroma());
    }

    /**
     * Create tones from a defined CAM hue and chroma.
     *
     * @param hue CAM hue
     * @param chroma CAM chroma
     * @return Tones matching hue and chroma.
     */
    public static TonalPalette fromHueAndChroma(float hue, float chroma) {
        return new TonalPalette(hue, chroma);
    }

    /**
     * Dynamically computed tones across the full range from 0 to 1000
     * @param shade expected shade from 0 (white) to 1000 (black)
     * @return Int representing color at new shade / tone
     */
    public int getAtTone(float shade) {
        int tone = (int) ((1000.0f - shade) / 10f);
        return tone(tone);
    }

    /**
     * Dynamically computed tones across the full range from 0 to 1000
     * @param shade expected shade from 0 (white) to 1000 (black)
     * @param luminanceFactor luminance factor to multiply by
     * @return Int representing color at new shade / tone
     */
    public int getAtTone(float shade, float luminanceFactor) {
        int tone = (int) ((1000.0f - shade) / 10f);
        tone = Math.round((float) tone * luminanceFactor);
        if (tone > 100) tone = 100;
        else if (tone < 0) tone = 0;
        return tone(tone);
    }

    /**
     * Create an ARGB color with CAM hue and chroma of this palette, and the provided tone.
     *
     * @param tone CAM tone (L*), measured from 0 to 100.
     * @return ARGB representation of a color with that tone.
     */
    private int tone(int tone) {
        return cache.computeIfAbsent(tone, k -> Cam.getInt(this.mHue, this.mChroma, (float) tone, Frame.DEFAULT));
    }

    // Predefined & precomputed tones
    public int getS0() {
        return this.allShades.get(0);
    }

    public int getS10() {
        return this.allShades.get(1);
    }

    public int getS50() {
        return this.allShades.get(2);
    }

    public int getS100() {
        return this.allShades.get(3);
    }

    public int getS200() {
        return this.allShades.get(4);
    }

    public int getS300() {
        return this.allShades.get(5);
    }

    public int getS400() {
        return this.allShades.get(6);
    }

    public int getS500() {
        return this.allShades.get(7);
    }

    public int getS600() {
        return this.allShades.get(8);
    }

    public int getS700() {
        return this.allShades.get(9);
    }

    public int getS800() {
        return this.allShades.get(10);
    }

    public int getS900() {
        return this.allShades.get(11);
    }

    public int getS1000() {
        return this.allShades.get(12);
    }

    public static final List<Integer> SHADE_KEYS = Arrays.asList(0, 10, 50, 100, 200, 300, 400, 500,
            600, 700, 800, 900, 1000);
}
