package com.bernaferrari.emojislider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public final class C2500c<StateType> {
    public final Map<StateType, C2498a<StateType>> f11202a = new HashMap();
    final Queue f11203b = new LinkedList();
    private final Map<StateType, Map<Class<?>, StateType>> f11206e = new HashMap();
    private final Map<StateType, Set<Class<?>>> f11207f = new HashMap();
    private final List<C2501d<StateType>> f11208g = new ArrayList();
    private final Queue f11209h = new LinkedList();
    private final String f11210i;
    public StateType f11204c;
    boolean f11205d;

    public C2500c(String str, StateType stateType) {
        this.f11210i = str;
        this.f11204c = stateType;
    }

    public final C2500c<StateType> m5860a(C2501d<StateType> c2501d) {
        if (!this.f11208g.contains(c2501d)) {
            this.f11208g.add(c2501d);
        }
        return this;
    }

    public final C2500c<StateType> m5861a(StateType stateType, Class<?> cls) {
        Set set = this.f11207f.get(stateType);
        if (set == null) {
            set = new HashSet();
            this.f11207f.put(stateType, set);
        }
        set.add(cls);
        return this;
    }

    public final C2500c<StateType> m5862a(StateType stateType, Class<?> cls, StateType stateType2) {
        Map map = this.f11206e.get(stateType);
        if (map == null) {
            map = new HashMap();
            this.f11206e.put(stateType, map);
        }
        map.put(cls, stateType2);
        return this;
    }

    final boolean m5863a(Object obj) {
        boolean z = true;
        this.f11205d = true;
        C2912a.m6550a();
        Set set = this.f11207f.get(this.f11204c);
        if (set == null || !set.contains(obj.getClass())) {
            Map map = this.f11206e.get(this.f11204c);
            if (map == null) {
                this.f11204c.toString();
            } else {
                Object obj2 = map.get(obj.getClass());
                if (obj2 == null) {
                    this.f11204c.toString();
                    obj.getClass();
                } else if (!this.f11202a.containsKey(obj2) || ((C2498a) this.f11202a.get(obj2)).mo1586a(obj2)) {
                    Object obj3 = this.f11204c;
//                    this.f11204c = obj2;
                    for (int i = 0; i < this.f11208g.size(); i++) {
                        ((C2501d) this.f11208g.get(i)).mo1583a(obj3, this.f11204c, obj);
                    }
                    obj3.toString();
                    this.f11204c.toString();
                    obj.getClass();
                    Queue linkedList = new LinkedList(this.f11209h);
                    while (!linkedList.isEmpty()) {
                        obj2 = linkedList.remove();
                        this.f11209h.remove();
                        if (m5863a(obj2)) {
                            break;
                        }
                    }
                    this.f11205d = false;
                    return z;
                } else {
                    obj2.toString();
                    ((C2498a) this.f11202a.get(obj2)).getClass();
                    obj.getClass();
                }
            }
        } else {
            this.f11209h.add(obj);
            obj.getClass();
            this.f11204c.toString();
        }
        z = false;
        this.f11205d = false;
        return z;
    }
}
